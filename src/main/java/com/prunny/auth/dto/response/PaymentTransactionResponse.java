package com.prunny.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private Long propertyId;
    private Long tenantId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
}
