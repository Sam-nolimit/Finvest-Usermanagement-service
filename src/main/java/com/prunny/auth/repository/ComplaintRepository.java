package com.prunny.auth.repository;


import com.prunny.auth.model.Complaint;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByTenant(User tenant);
    List<Complaint> findByProperty(Property property);
}

