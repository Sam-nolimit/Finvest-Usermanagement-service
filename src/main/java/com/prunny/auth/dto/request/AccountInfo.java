package com.prunny.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private String firstName;
    //    @Size(min = 3, message = "Last name can not be less than 3")
    private String lastName;

    private String email;
    //    @Size(min = 6, message = "Password should have at least 6 characters")
    //    @Size(min = 10, message = "Phone number should have at least 10 characters")
    private String phoneNumber;

    private String gender;
}
