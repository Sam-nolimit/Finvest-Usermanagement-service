package com.prunny.auth.dto.response;

import com.prunny.auth.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private Boolean isVerified;
    private String firstName;
    private String lastName;
    private String stateOfOrigin;
    private String accountNumber;
    private String accountName;
    private String bankCode;
    private String recipientCode;
    private String bvn;
    private String phoneNumber;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
