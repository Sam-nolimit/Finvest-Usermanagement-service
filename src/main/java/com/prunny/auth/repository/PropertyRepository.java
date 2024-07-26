package com.prunny.auth.repository;

import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByIsAvailable(boolean isAvailable);
    List<Property> findByLandlord(User landLord);
}