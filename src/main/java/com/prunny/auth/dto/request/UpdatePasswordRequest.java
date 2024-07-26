package com.prunny.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String newPassword;
    @NotNull(message = "Confirm new password is required")
    @NotEmpty
    private String confirmPassword;

    private String oldPassword;

}
