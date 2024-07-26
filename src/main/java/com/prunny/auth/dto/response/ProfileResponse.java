package com.prunny.auth.dto.response;

import com.prunny.auth.model.Complaint;
import com.prunny.auth.model.Profile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileResponse {
    private String stateOfOrigin;
    private String accountNumber;
    private String accountName;
    private String bankCode;
    private String recipientCode;


    public ProfileResponse(Profile profile) {
        this.accountNumber = profile.getAccountNumber();
        this.accountName = profile.getAccountName();
        this.bankCode = profile.getBankCode();
        this.recipientCode = profile.getRecipientCode();

    }

}