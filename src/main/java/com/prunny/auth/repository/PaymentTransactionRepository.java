package com.prunny.auth.repository;

import com.prunny.auth.model.PaymentTransaction;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    List<PaymentTransaction> findByUser(User user);
}