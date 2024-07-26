package com.prunny.auth.controller;

import com.prunny.auth.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePayment(@RequestParam String email, @RequestParam Long propertyId, @RequestParam double amount) {
        try {
            String authorizationUrl = paymentService.initiatePayment(email, propertyId, amount);
            return ResponseEntity.ok(authorizationUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error initiating payment: " + e.getMessage());
        }
    }
}
