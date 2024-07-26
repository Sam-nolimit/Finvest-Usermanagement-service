package com.prunny.auth.repository;

import com.prunny.auth.enums.Role;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByBvn(String bvn);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByBvn(String bvn);
    Optional<User> findByPhoneNumber(String phoneNumber);

    User findByEmailAndRole(String email, Role role);
}

