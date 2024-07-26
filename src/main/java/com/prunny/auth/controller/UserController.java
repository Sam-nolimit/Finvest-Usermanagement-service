package com.prunny.auth.auth;

import com.prunny.auth.dto.request.*;
import com.prunny.auth.dto.response.AuthenticationResponse;
import com.prunny.auth.dto.response.UserResponse;
import com.prunny.auth.exception.AuthenticationFailedException;
import com.prunny.auth.exception.InvalidOtpException;
import com.prunny.auth.exception.PasswordIncorrect;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.registerTenant(request));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.createAdmin(request));
    }

    @PostMapping("/register-landlord")
    public ResponseEntity<UserResponse> registerLandlord(
            @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.createLandlord(request));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginRequest request
    )   {
        return ResponseEntity.ok(service.loginUser(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody   ForgotPasswordRequest request
    ) {
        service.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody PasswordResetRequest request
    ) throws InvalidOtpException {
        service.resetPassword(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(@RequestBody LogoutRequest request) {
        service.logout(request);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .message("User logged out successfully")
                .timestamp(LocalDateTime.now())
                .status("success")
                .build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> editUserDetails(
            @PathVariable Long id,
            @RequestBody UserRequest request
    ) {
        UserResponse updatedUser = service.editUserDetails(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable Long id,
            @RequestBody UpdatePasswordRequest request
    ) {
        service.updateUserPassword(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}