package com.prunny.auth.service.impl;

import com.prunny.auth.config.JwtUtil;
import com.prunny.auth.dto.EmailDetails;
import com.prunny.auth.dto.request.*;
import com.prunny.auth.dto.response.AuthenticationResponse;
import com.prunny.auth.dto.response.UserResponse;
import com.prunny.auth.enums.Role;
import com.prunny.auth.exception.*;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.EmailService;
import com.prunny.auth.service.OtpService;
import com.prunny.auth.service.UserService;
import com.prunny.auth.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.exceptions.TemplateInputException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final Set<String> tokenBlacklist = new HashSet<>();
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private Map<String, String> otpStorage = new HashMap<>();
    @Override
    public UserResponse registerTenant(RegisterRequest request) {
        try {
            validateEmail(request.getEmail());
            validatePassword(request.getPassword());
            validateBvn(request.getBvn());
            validatePhoneNumber(request.getPhoneNumber());

            Optional<User> existingBvn = userRepository.findByBvn(request.getBvn());
            Optional<User> existingPhoneNumber = userRepository.findByPhoneNumber(request.getPhoneNumber());

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistException("User with email already exist");
            }

            if (existingBvn.isPresent()) {
                throw new BvnExistsException("This BVN already exists");
            }

            if (existingPhoneNumber.isPresent()) {
                throw new PhoneNumberExistsException("This Phone Number already exists");
            }

            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .bvn(request.getBvn())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .stateOfOrigin(request.getStateoforigin())
                    .phoneNumber(request.getPhoneNumber())
                    .isVerified(false)
                    .role(Role.TENANT)
                    .build();
            userRepository.save(user);

            String otp = otpService.generateOtp(user);
            otpStorage.put(user.getEmail(), otp);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("ACCOUNT CREATION")
                    .templateName("email-template")
                    .model(Map.of("name", user.getFirstName() + " " + user.getLastName(), "otp", otp))
                    .build();

            emailService.sendEmails(emailDetails);

            return UserResponse.builder()
                    .id(user.getId())
                    .isVerified(user.getIsVerified())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .bvn(user.getBvn())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .modifiedAt(user.getModifiedAt())
                    .build();
        } catch (BvnExistsException | UserAlreadyExistException | PhoneNumberExistsException e) {
            throw e;
        } catch (TemplateInputException e) {
            throw new BadRequestException("Failed to process email template: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Registration failed due to unexpected error: " + e.getMessage());
        }
    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createAdmin(RegisterRequest userRequest) throws jakarta.mail.MessagingException {
        try {
            Optional<User> existingPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new UserAlreadyExistException("Email already exists");
            }
            if (existingPhoneNumber.isPresent()) {
                throw new PhoneNumberExistsException("This Phone Number already exists");
            }
            if (!AccountUtils.validatePassword(userRequest.getPassword(), userRequest.getConfirmPassword())){
                throw new UserPasswordMismatchException("Password does not match");
            }
            if (existsByEmail(userRequest.getEmail())){
                throw new BadRequestException("Error: Email is already taken!");
            }
            if (!isValidEmail(userRequest.getEmail())){
                throw new BadRequestException("Error: Email must be valid");
            }
            if (userRequest.getPassword().length() < 8 || userRequest.getConfirmPassword().length() < 8){
                throw new BadRequestException("Password is too short, should be a minimum of 8 characters long");
            }


            User newAdmin = User.builder()
                    .firstName(userRequest.getFirstname())
                    .lastName(userRequest.getLastname())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .bvn(userRequest.getBvn())
                    .stateOfOrigin(userRequest.getStateoforigin())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .isVerified(true)
                    .role(Role.ADMIN)
                    .build();
            User savedAdmin = userRepository.save(newAdmin);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(newAdmin.getEmail())
                    .subject("ACCOUNT CREATION")
                    .templateName("email-template-admin")
                    .model(Map.of("name", newAdmin.getFirstName() + " " + newAdmin.getLastName()))
                    .build();

            emailService.sendHtmlEmail(emailDetails);

            return UserResponse.builder()
                    .id(savedAdmin.getId())
                    .isVerified(savedAdmin.getIsVerified())
                    .firstName(savedAdmin.getFirstName())
                    .lastName(savedAdmin.getLastName())
                    .phoneNumber(savedAdmin.getPhoneNumber())
                    .email(savedAdmin.getEmail())
                    .createdAt(savedAdmin.getCreatedAt())
                    .modifiedAt(savedAdmin.getModifiedAt())
                    .build();

        } catch (BvnExistsException | UserAlreadyExistException | PhoneNumberExistsException e) {
            throw e;
        } catch (TemplateInputException e) {
            throw new BadRequestException("Failed to process email template: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Registration failed due to unexpected error: " + e.getMessage());
        }
    }

    @Override
    public UserResponse createLandlord(RegisterRequest userRequest) throws jakarta.mail.MessagingException {
        try {
            Optional<User> existingPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new UserAlreadyExistException("Email already exists");
            }
            if (existingPhoneNumber.isPresent()) {
                throw new PhoneNumberExistsException("This Phone Number already exists");
            }
            if (!AccountUtils.validatePassword(userRequest.getPassword(), userRequest.getConfirmPassword())){
                throw new UserPasswordMismatchException("Password does not match");
            }
            if (existsByEmail(userRequest.getEmail())){
                throw new BadRequestException("Error: Email is already taken!");
            }
            if (!isValidEmail(userRequest.getEmail())){
                throw new BadRequestException("Error: Email must be valid");
            }
            if (userRequest.getPassword().length() < 8 || userRequest.getConfirmPassword().length() < 8){
                throw new BadRequestException("Password is too short, should be a minimum of 8 characters long");
            }

            User newLandlord = User.builder()
                    .firstName(userRequest.getFirstname())
                    .lastName(userRequest.getLastname())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .bvn(userRequest.getBvn())
                    .stateOfOrigin(userRequest.getStateoforigin())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .isVerified(false)
                    .role(Role.LANDLORD)
                    .build();
            User savedAdmin = userRepository.save(newLandlord);

            String otp = otpService.generateOtp(newLandlord);
            otpStorage.put(newLandlord.getEmail(), otp);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(newLandlord.getEmail())
                    .subject("ACCOUNT CREATION")
                    .templateName("email-template-landlord")
                    .model(Map.of("name", newLandlord.getFirstName() + " " + newLandlord.getLastName(), "otp", otp))
                    .build();

            emailService.sendHtmlEmail(emailDetails);

            return UserResponse.builder()
                    .id(savedAdmin.getId())
                    .isVerified(savedAdmin.getIsVerified())
                    .firstName(savedAdmin.getFirstName())
                    .lastName(savedAdmin.getLastName())
                    .phoneNumber(savedAdmin.getPhoneNumber())
                    .email(savedAdmin.getEmail())
                    .createdAt(savedAdmin.getCreatedAt())
                    .modifiedAt(savedAdmin.getModifiedAt())
                    .build();
        } catch (BvnExistsException | UserAlreadyExistException | PhoneNumberExistsException e) {
                throw e;
                } catch (TemplateInputException e) {
                throw new BadRequestException("Failed to process email template: " + e.getMessage());
                } catch (Exception e) {
                throw new BadRequestException("Registration failed due to unexpected error: " + e.getMessage());
                }
                }
@Override
    public UserResponse verifyUser(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.setIsVerified(true);
            userRepository.save(user);
            otpStorage.remove(email);

            return UserResponse.builder()
                    .id(user.getId())
                    .isVerified(user.getIsVerified())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .modifiedAt(user.getModifiedAt())
                    .build();
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }
    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            if (!authenticate.isAuthenticated()) {
                throw new UserPasswordMismatchException("Wrong email or password");
            }

            Optional<User> userDetails = userRepository.findByEmail(loginRequest.getEmail());
            if (!userDetails.get().getIsVerified()) {
                throw new BadRequestException("Account not verified");
            }

            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = "Bearer " + jwtUtil.generateToken(loginRequest.getEmail());

            UserResponse userDto = UserResponse.builder()
                    .id(userDetails.get().getId())
                    .isVerified(userDetails.get().getIsVerified())
                    .firstName(userDetails.get().getFirstName())
                    .lastName(userDetails.get().getLastName())
                    .phoneNumber(userDetails.get().getPhoneNumber())
                    .email(userDetails.get().getEmail())
                    .build();

            return new LoginResponse(token, userDto);
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Wrong email or password");
        }
    }

    @Override
    public UserResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new CustomNotFoundException("User with provided Email not found");
        }

        User user = userOptional.get();
        String otp = otpService.generateOtp(user);
        otpStorage.put(user.getEmail(), otp);

        Map<String, Object> model = new HashMap<>();
        model.put("otp", otp);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(forgotPasswordRequest.getEmail())
                .subject("PASSWORD RESET OTP")
                .templateName("password-reset-email")
                .model(model)
                .build();

        try {
            emailService.sendEmailWithThymeleaf(emailDetails);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send the password reset email");
        }

        return UserResponse.builder()
                .id(user.getId())
                .isVerified(user.getIsVerified())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    public UserResponse resetPassword(PasswordResetRequest passwordRequest) {
        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
            throw new CustomNotFoundException("Password do not match");
        }

        String email = passwordRequest.getEmail();

        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(passwordRequest.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordRequest.getNewPassword().length() < 8 || passwordRequest.getConfirmPassword().length() < 8) {
            throw new BadRequestException("Error: Password is too short");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        otpStorage.remove(email);

        return  UserResponse.builder()
                .id(user.getId())
                .isVerified(user.getIsVerified())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserResponse editUserDetails(Long userId, UserRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        user.setFirstName(userUpdateRequest.getFirstname());
        user.setLastName(userUpdateRequest.getLastname());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setEmail(userUpdateRequest.getEmail());
        user.setStateOfOrigin(userUpdateRequest.getStateoforigin());

        User updatedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(updatedUser.getId())
                .isVerified(updatedUser.getIsVerified())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .phoneNumber(updatedUser.getPhoneNumber())
                .email(updatedUser.getEmail())
                .createdAt(updatedUser.getCreatedAt())
                .modifiedAt(updatedUser.getModifiedAt())
                .build();
    }


    @Override
    public UserResponse updateUserPassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getConfirmPassword())) {
            throw new BadRequestException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return  UserResponse.builder()
                .id(user.getId())
                .isVerified(user.getIsVerified())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }


    public void logout(LogoutRequest request) {
        // Add the token to the blacklist to invalidate it
        tokenBlacklist.add(request.getToken());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users due to unexpected error: " + e.getMessage());
        }
    }
    @Override
    public UserResponse getUserById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isVerified(user.getIsVerified())
                .phoneNumber(user.getPhoneNumber())
                .stateOfOrigin(user.getStateOfOrigin())
                .accountNumber(user.getProfile() != null ? user.getProfile().getAccountNumber() : null)
                .accountName(user.getProfile() != null ? user.getProfile().getAccountName() : null)
                .bankCode(user.getProfile() != null ? user.getProfile().getBankCode() : null)
                .recipientCode(user.getProfile() != null ? user.getProfile().getRecipientCode() : null)
                .bvn(user.getBvn())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    public boolean isTokenValid(String token) {
        // Check if the token is in the blacklist
        return !tokenBlacklist.contains(token);
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

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !isValidPhoneNumber(phoneNumber)) {
            throw new BadRequestException("Error: Phone number must be a valid 11-digit number");
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{11}");
    }

    private boolean isValidBvn(String bvn) {
        return bvn.matches("\\d{11}");
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean existsByBvn(String bvn) {
        return userRepository.existsByBvn(bvn);
    }






}
