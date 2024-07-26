package com.prunny.auth.dto.request;

import com.prunny.auth.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRequest {
    private String phoneNumber;
    private String firstname;
    private String lastname;
    private String email;
    private String stateoforigin;

    // Getters and Setters
}

