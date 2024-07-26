package com.prunny.auth.controller;

import com.prunny.auth.dto.request.PaymentTransactionRequest;
import com.prunny.auth.dto.response.PaymentTransactionResponse;
import com.prunny.auth.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class PaymentTransactionController {
    private final PaymentTransactionService transaction;


    @PostMapping
    public ResponseEntity<PaymentTransactionResponse> paymentTransaction(@RequestBody PaymentTransactionRequest paymentTransactionRequest) {
        PaymentTransactionResponse paymentTransactionResponse = transaction.paymentTransaction(paymentTransactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentTransactionResponse);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentTransactionResponse> getByTransactionId(@PathVariable String transactionId) {
        PaymentTransactionResponse paymentTransactionResponse = transaction.getByTransactionId(transactionId);
        return ResponseEntity.ok(paymentTransactionResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<PaymentTransactionResponse>> getByTransactionId(@PathVariable Long userId) {
        List<PaymentTransactionResponse> paymentTransactionResponse = transaction.findByUserId(userId);
        return ResponseEntity.ok(paymentTransactionResponse);
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransactionResponse>> findAll() {
        List<PaymentTransactionResponse> payments = transaction.findAll();
        return ResponseEntity.ok(payments);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteByTransactionId(@PathVariable String transactionId) {
        transaction.deleteByTransactionId(transactionId);
        return ResponseEntity.noContent().build();
    }
}
