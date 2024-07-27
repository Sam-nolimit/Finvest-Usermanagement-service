package com.prunny.auth.service.impl;

import com.prunny.auth.dto.request.ComplaintRequest;
import com.prunny.auth.dto.response.ComplaintResponse;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.model.Complaint;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.ComplaintRepository;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public ComplaintResponse createComplaint(ComplaintRequest complaintRequest) {
        User tenant = userRepository.findById(complaintRequest.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        Property property = propertyRepository.findById(complaintRequest.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        try {

            Complaint complaint = Complaint.builder()
                    .tenant(tenant)
                    .property(property)
                    .description(complaintRequest.getDescription())
                    .createdAt(LocalDateTime.now())
                    .build();

            complaint = complaintRepository.save(complaint);
            return new ComplaintResponse(complaint);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create complaint", e);
        }
    }

    @Override
    public List<ComplaintResponse> getComplaintsByTenant(Long tenantId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        try {
            return complaintRepository.findByTenant(tenant).stream()
                    .map(ComplaintResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get complaints by tenant", e);
        }
    }

    @Override
    public List<ComplaintResponse> getComplaintsByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        try {
            return complaintRepository.findByProperty(property).stream()
                    .map(ComplaintResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get complaints by property", e);
        }
    }
}
