package com.prunny.auth.dto.request;


import lombok.Data;

@Data
public class ProfileRequest {
    private String stateOfOrigin;
    private String accountNumber;
    private String accountName;
    private String bankCode;
    private String recipientCode;
    private String bvn;
}

