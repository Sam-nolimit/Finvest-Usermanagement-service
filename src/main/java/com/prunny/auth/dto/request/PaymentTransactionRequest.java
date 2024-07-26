package com.prunny.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionRequest {
    private Long propertyId;
    private Long tenantId;
    private double amount;
    private String paymentMethod;
    private String transactionId;
}

