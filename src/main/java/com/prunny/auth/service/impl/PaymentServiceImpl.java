package com.prunny.auth.service.impl;

import com.prunny.auth.service.PaymentService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${paystack_secret_key:paystack}")
    private String PAYSTACK_SECRET_KEY;

    @Value("${paystack_initiate_payment_url:paystack}")
    private String PAYSTACK_INITIATE_PAYMENT_URL;


    public String initiatePayment(String email, Long propertyId, double amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = getMapHttpEntity(email, propertyId, amount);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(PAYSTACK_INITIATE_PAYMENT_URL, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return new JSONObject(response.getBody()).getJSONObject("data").getString("authorization_url");
            } else {
                logger.error("Failed to initiate payment: {}", response.getBody());
                throw new RuntimeException("Failed to initiate payment. Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error occurred while initiating payment", e);
            throw new RuntimeException("Failed to initiate payment due to unexpected error: " + e.getMessage());
        }
    }

    private HttpEntity<Map<String, Object>> getMapHttpEntity(String email, Long propertyId, double amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + PAYSTACK_SECRET_KEY);
        headers.set("Content-Type", "application/json");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("property_id", propertyId);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("amount", (int) (amount * 100));;
        body.put("metadata", metadata);

        return new HttpEntity<>(body, headers);
    }
    

}
