package com.prunny.auth.service.impl;

import com.prunny.auth.dto.request.PaymentTransactionRequest;
import com.prunny.auth.dto.response.PaymentTransactionResponse;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.model.PaymentTransaction;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.PaymentTransactionRepository;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public PaymentTransactionResponse paymentTransaction(PaymentTransactionRequest paymentTransactionRequest) {
        try {
            User tenant = userRepository.findById(paymentTransactionRequest.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

            Property property = propertyRepository.findById(paymentTransactionRequest.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setUser(tenant);
            paymentTransaction.setProperty(property);
            paymentTransaction.setAmount(paymentTransactionRequest.getAmount());
            paymentTransaction.setPaymentMethod(paymentTransactionRequest.getPaymentMethod());
            paymentTransaction.setPaymentStatus("COMPLETED");
            paymentTransaction.setTransactionId(paymentTransaction.getTransactionId());
            paymentTransaction = paymentTransactionRepository.save(paymentTransaction);

            property.setAvailable(false);
            propertyRepository.save(property);

            return new PaymentTransactionResponse(
                    paymentTransaction.getId(),
                    paymentTransaction.getProperty().getId(),
                    paymentTransaction.getUser().getId(),
                    paymentTransaction.getAmount(),
                    paymentTransaction.getPaymentDate(),
                    paymentTransaction.getPaymentMethod(),
                    paymentTransaction.getPaymentStatus(),
                    paymentTransaction.getTransactionId()
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment transaction", e);
        }
    }

    @Override
    public PaymentTransactionResponse getByTransactionId(String transactionId) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            return new PaymentTransactionResponse(
                    paymentTransaction.getId(),
                    paymentTransaction.getProperty().getId(),
                    paymentTransaction.getUser().getId(),
                    paymentTransaction.getAmount(),
                    paymentTransaction.getPaymentDate(),
                    paymentTransaction.getPaymentMethod(),
                    paymentTransaction.getPaymentStatus(),
                    paymentTransaction.getTransactionId()
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve payment transaction", e);
        }
    }

    @Override
    public List<PaymentTransactionResponse> findAll() {
        try {
            return paymentTransactionRepository.findAll().stream()
                    .map(paymentTransaction -> new PaymentTransactionResponse(
                            paymentTransaction.getId(),
                            paymentTransaction.getProperty().getId(),
                            paymentTransaction.getUser().getId(),
                            paymentTransaction.getAmount(),
                            paymentTransaction.getPaymentDate(),
                            paymentTransaction.getPaymentMethod(),
                            paymentTransaction.getPaymentStatus(),
                            paymentTransaction.getTransactionId()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve all payment transactions", e);
        }
    }

    @Override
    public List<PaymentTransactionResponse> findByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            List<PaymentTransaction> transactions = paymentTransactionRepository.findByUser(user);

            return transactions.stream()
                    .map(paymentTransaction -> new PaymentTransactionResponse(
                            paymentTransaction.getId(),
                            paymentTransaction.getProperty().getId(),
                            paymentTransaction.getUser().getId(),
                            paymentTransaction.getAmount(),
                            paymentTransaction.getPaymentDate(),
                            paymentTransaction.getPaymentMethod(),
                            paymentTransaction.getPaymentStatus(),
                            paymentTransaction.getTransactionId()
                    ))
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve payment transactions by user", e);
        }
    }

    @Override
    public void deleteByTransactionId(String transactionId) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            paymentTransactionRepository.delete(paymentTransaction);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete payment transaction", e);
        }
    }
}
