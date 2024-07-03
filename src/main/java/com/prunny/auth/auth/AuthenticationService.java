package com.prunny.auth.auth;

import com.prunny.auth.exception.*;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.JwtService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    public AuthenticationResponse register(RegisterRequest request) {
//        Optional<User> existingUser = repository.findByEmail(request.getEmail());
//        if (existingUser.isPresent()) {
//            throw new AlreadyExistsException("This Email already exists");
//        }
//        if(!isValidEmail(request.getEmail())){
//            throw new BadRequestException("Error: Email must be valid");
//        }
//
//        if(request.getPassword().length() < 8  ){
//            throw new BadRequestException("Password is too short, should be minimum of 8 character long");
//        }
//
//        var user = User.builder()
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .stateoforigin(request.getStateoforigin())
//                .bvn(request.getBvn())
//                .role(Role.USER)
//                .build();
//        repository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
//    }

    public AuthenticationResponse register(RegisterRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());

        Optional<User> existingUser = repository.findByEmail(request.getEmail());
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
    }

    private void validateEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new BadRequestException("Error: Email must be valid");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("Password is too short, should be minimum of 8 characters long");
        }
    }

    public AuthenticationResponse registerAdmin(RegisterAdminRequest request) {
        Optional<User> existingUser = repository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("This Email already exists");
        }

        if(!isValidEmail(request.getEmail())){
            throw new BadRequestException("Error: Email must be valid");
        }

        if(request.getPassword().length() < 8  ){
            throw new BadRequestException("Password is too short, should be minimum of 8 character long");
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
                throw new PasswordIncorrect("The password is incorrect ");

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

    private boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if (email == null) {
            throw new BadRequestException("Error: Email cannot be null");
        }

        return email.matches(regex);
    }



    private boolean existsByMail(String email) {
        return repository.existsByEmail(email);
    }
}
