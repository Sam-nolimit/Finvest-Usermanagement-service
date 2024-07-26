package com.prunny.auth.service;

import com.prunny.auth.dto.request.*;
import com.prunny.auth.dto.response.AuthenticationResponse;
import com.prunny.auth.dto.response.ReviewResponse;
import com.prunny.auth.dto.response.UserResponse;
import com.prunny.auth.exception.InvalidOtpException;
import com.prunny.auth.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    LoginResponse loginUser(LoginRequest loginRequest);
    UserResponse registerTenant(RegisterRequest request);
    UserResponse createAdmin(RegisterRequest userRequest) throws MessagingException;
    UserResponse createLandlord(RegisterRequest userRequest) throws MessagingException;
    UserResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    UserResponse verifyUser(String email, String otp);
    UserResponse resetPassword(PasswordResetRequest passwordRequest);
    UserResponse editUserDetails(Long userId, UserRequest userUpdateRequest);
    UserResponse updateUserPassword(Long userId, UpdatePasswordRequest updatePasswordRequest);
    void logout(LogoutRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id) throws ResourceNotFoundException;
    void deleteUser(Long id) throws ResourceNotFoundException;


}
