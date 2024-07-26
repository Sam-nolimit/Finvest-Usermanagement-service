package com.prunny.auth.service;

import com.prunny.auth.dto.request.PaymentTransactionRequest;
import com.prunny.auth.dto.response.PaymentTransactionResponse;

import java.util.List;

public interface PaymentTransactionService {
    PaymentTransactionResponse paymentTransaction(PaymentTransactionRequest paymentTransactionRequest);
    PaymentTransactionResponse getByTransactionId(String transactionId);
    List<PaymentTransactionResponse> findAll();
    void deleteByTransactionId(String transactionId);
    List<PaymentTransactionResponse> findByUserId(Long userId);
}
