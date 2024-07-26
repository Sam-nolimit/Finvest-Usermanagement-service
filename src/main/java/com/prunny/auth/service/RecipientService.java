package com.prunny.auth.service;

public interface RecipientService {
    String createRecipient(String name, String accountNumber, String bankCode);
    void creditLandlord(String transactionReference, Long propertyId, double amount);
}
