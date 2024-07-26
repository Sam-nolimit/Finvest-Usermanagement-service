package com.prunny.auth.service;

public interface PaymentService {

    String initiatePayment(String email, Long propertyId, double amount);
}
