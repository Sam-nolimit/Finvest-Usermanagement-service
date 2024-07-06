package com.prunny.auth.auth;

import com.prunny.auth.exception.*;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.JwtService;
import com.prunny.auth.service.OtpService;
import com.prunny.auth.user.Role;
import com.prunny.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthenticationResponse register(RegisterRequest request) {
        try {
            validateEmail(request.getEmail());
            validatePassword(request.getPassword());
            validateBvn(request.getBvn());

            Optional<User> existingUser = repository.findByEmail(request.getEmail());
            Optional<User> existingBvn = repository.findByBvn(request.getBvn());

            if (existingBvn.isPresent()) {
                throw new BvnExistsException("This BVN already exists");
            }

            if (existingUser.isPresent()) {
                throw new AlreadyExistsException("This Email already exists");
            }

            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .stateoforigin(request.getStateoforigin())
                    .bvn(request.getBvn())
                    .role(Role.USER)
                    .build();
            repository.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (BvnExistsException | AlreadyExistsException e) {
            // Handle specific exceptions
            throw e;
        } catch (Exception e) {
            // Handle other exceptions
            throw new BadRequestException("Registration failed due to unexpected error: " + e.getMessage());
        }
    }

    public AuthenticationResponse registerAdmin(RegisterAdminRequest request) {
        try {
            validateEmail(request.getEmail());
            validatePassword(request.getPassword());
            validateBvn(request.getBvn());

            Optional<User> existingUser = repository.findByEmail(request.getEmail());
            Optional<User> existingBvn = repository.findByBvn(request.getBvn());

            if (existingBvn.isPresent()) {
                throw new BvnExistsException("This BVN already exists");
            }

            if (existingUser.isPresent()) {
                throw new AlreadyExistsException("This Email already exists");
            }

            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .stateoforigin(request.getStateoforigin())
                    .bvn(request.getBvn())
                    .role(Role.ADMIN)
                    .build();
            repository.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (BvnExistsException | AlreadyExistsException e) {
            // Handle specific exceptions
            throw e;
        } catch (Exception e) {
            // Handle other exceptions
            throw new BadRequestException("Registration failed due to unexpected error: " + e.getMessage());
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws PasswordIncorrect, AuthenticationFailedException {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("This User does not exist"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            if (!authentication.isAuthenticated()) {
                throw new PasswordIncorrect("The password is incorrect");
            }
        } catch (BadCredentialsException e) {
            // Ensure that if an authentication error occurs, it's a password issue, not a user not found issue.
            throw new AuthenticationFailedException("The email or password is incorrect");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void forgotPassword(ForgottenPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("This User does not exist"));

        // Generate and send OTP
        String otp = otpService.generateOtp(user);
        otpService.sendOtp(user, otp);
    }

    public void resetPassword(ResetPasswordRequest request) throws InvalidOtpException {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("This User does not exist"));

        if (!otpService.verifyOtp(user, request.getOtp())) {
            throw new InvalidOtpException("The OTP is invalid or expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);

//         Notify the user
        otpService.sendPasswordResetConfirmation(user.getEmail());
    }

    public void verifyOtp(VerifyOtpRequest request) throws InvalidOtpException {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("This User does not exist"));

        if (!otpService.verifyOtp(user, request.getOtp())) {
            throw new InvalidOtpException("The OTP is invalid or expired");
        }

        // OTP verified successfully
    }


    private void validateEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new BadRequestException("Error: Email must be valid");
        }
    }

    private void validateBvn(String bvn) {
        // BVN must be 11 digits and must be a number
        if (bvn == null || !isValidBvn(bvn)) {
            throw new BadRequestException("Error: BVN must be a valid 11-digit number");
        }
    }

    private void validatePassword(String password) {
        // Password should contain string, number, and symbols
        if (password.length() < 8) {
            throw new BadRequestException("Password is too short, should be a minimum of 8 characters long");
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if (email == null) {
            throw new BadRequestException("Error: Email cannot be null");
        }

        return email.matches(regex);
    }

    private boolean isValidBvn(String bvn) {
        return bvn.matches("\\d{11}");
    }

    private boolean existsByMail(String email) {
        return repository.existsByEmail(email);
    }

    private boolean existsByBvn(String bvn) {
        return repository.existsByBvn(bvn);
    }
}
