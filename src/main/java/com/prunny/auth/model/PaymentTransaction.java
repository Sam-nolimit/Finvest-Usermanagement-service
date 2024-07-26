
package com.prunny.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String senderEmail;

    private String recipientName;

    @ManyToOne
    private Property property;

    private double amount;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;
}

