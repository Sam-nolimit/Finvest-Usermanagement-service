package com.prunny.auth.repository;

import com.prunny.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByBvn(String bvn);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByBvn(String bvn);
    Optional<User> findByPhoneNumber(String phoneNumber);
}

