package com.prunny.auth.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdminRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private String stateoforigin;
    private String bvn;
}
