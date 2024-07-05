package com.prunny.auth.repository;

import com.prunny.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    boolean existsByBvn(String bvn);

    Optional<User> findByEmail(String email);

    Optional<User> findByBvn(String bvn);

}

