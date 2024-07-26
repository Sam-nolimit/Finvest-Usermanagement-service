package com.prunny.auth.repository;

import com.prunny.auth.model.Property;
import com.prunny.auth.model.Save;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SaveRepository extends JpaRepository<Save, Long> {
    boolean existsByUserAndProperty(User user, Property property);
    List<Save> findByPropertyId(Long propertyId);
    List<Save> findByUserId(Long userId);
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}
