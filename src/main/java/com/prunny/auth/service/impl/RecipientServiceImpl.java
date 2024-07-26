package com.prunny.auth.service.impl;

import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.model.PaymentTransaction;
import com.prunny.auth.model.Profile;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.PaymentTransactionRepository;
import com.prunny.auth.repository.ProfileRepository;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecipientServiceImpl implements RecipientService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${paystack_secret_key:paystack}")
    private String PAYSTACK_SECRET_KEY;

    @Value("${paystack_create_recipient-url:paystack}")
    private String PAYSTACK_CREATE_RECIPIENT_URL;

    @Value("${ paystack_transaction_url:paystack}")
    private String PAYSTACK_TRANSACTION_URL;

    @Value("${paystack_initiate_transfer-url:paystack}")
    private String PAYSTACK_INITIATE_TRANSFER_URL;

    public String createRecipient(String name, String accountNumber, String bankCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + PAYSTACK_SECRET_KEY);
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("type", "nuban");
        body.put("name", name);
        body.put("account_number", accountNumber);
        body.put("bank_code", bankCode);
        body.put("currency", "NGN");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(PAYSTACK_CREATE_RECIPIENT_URL, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new JSONObject(response.getBody()).getJSONObject("data").getString("recipient_code");
        } else {
            throw new RuntimeException("Failed to create transfer recipient");
        }
    }

    public void creditLandlord(String transactionReference, Long propertyId, double amount) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User landlord = property.getLandlord();

        Profile profile = profileRepository.findByUser(landlord);

        String name = landlord.getFirstName() + " " + landlord.getLastName();
        if (profile.getRecipientCode() == null) {
            profile.setRecipientCode(createRecipient(
                    name, profile.getAccountNumber(), profile.getBankCode()));
            userRepository.save(landlord);
        }

        double landlordAmount = amount * 0.95; // Deduct 5% charge

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + PAYSTACK_SECRET_KEY);
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("source", "balance");
        body.put("amount", (int) landlordAmount);
        body.put("recipient", profile.getRecipientCode());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(PAYSTACK_INITIATE_TRANSFER_URL, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            createAndSaveTransaction(landlord.getEmail(), propertyId, amount, "SUCCESSFUL", transactionReference);
        } else {
            throw new RuntimeException("Failed to transfer funds to landlord");
        }
    }

    private void createAndSaveTransaction(String email, Long propertyId, double amount, String status, String transactionReference) {
        User landlord = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Query Paystack for transaction details
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + PAYSTACK_SECRET_KEY);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                PAYSTACK_TRANSACTION_URL + transactionReference,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject transactionData = new JSONObject(response.getBody()).getJSONObject("data");
            String senderEmail = transactionData.getJSONObject("customer").getString("email");

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setUser(landlord);
            paymentTransaction.setProperty(property);
            paymentTransaction.setAmount(amount);
            paymentTransaction.setPaymentMethod("PAYSTACK");
            paymentTransaction.setPaymentStatus(status);
            paymentTransaction.setTransactionId(transactionReference);
            paymentTransaction.setSenderEmail(senderEmail);
            paymentTransactionRepository.save(paymentTransaction);
            if ("COMPLETED".equals(status)) {
                property.setAvailable(false);
                propertyRepository.save(property);
            }
        } else {
            throw new RuntimeException("Failed to retrieve transaction details. Status Code: " + response.getStatusCode());
        }
    }
}
