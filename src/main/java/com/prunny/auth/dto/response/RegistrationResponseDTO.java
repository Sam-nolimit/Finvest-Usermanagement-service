package com.prunny.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Data
public class RegistrationResponseDTO {
    private String firstName;
    private String lastName;
    private String designationOfMinistry;
    private String email;
    private String message;

    public RegistrationResponseDTO(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }




}

