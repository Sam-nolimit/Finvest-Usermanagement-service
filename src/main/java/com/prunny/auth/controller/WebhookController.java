package com.prunny.auth.controller;

import com.prunny.auth.service.PaymentService;
import com.prunny.auth.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final RecipientService recipientService;



    @PostMapping("/paystack")
    @ResponseStatus(HttpStatus.OK)
    public void handlePaystackWebhook(@RequestBody String payload) {
        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if ("charge.success".equals(eventType)) {
            JSONObject data = event.getJSONObject("data");
            String transactionReference = data.getString("reference");
            double amount = data.getDouble("amount") / 100.0;

            // Retrieve custom metadata
            Long propertyId = data.getJSONObject("metadata").getLong("property_id");

            // Credit the landlord
            recipientService.creditLandlord(transactionReference, propertyId, amount);
        }
    }
}
