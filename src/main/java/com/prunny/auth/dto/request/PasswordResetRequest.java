package com.prunny.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PasswordResetRequest {
    private String otp;
    private String email;
    @NotNull(message = "New password  is required")
    @NotEmpty
    private String newPassword;
    @NotNull(message = "Confirm new password is required")
    @NotEmpty
    private String confirmPassword;



    }


