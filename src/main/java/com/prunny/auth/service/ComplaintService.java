package com.prunny.auth.service;

import com.prunny.auth.dto.request.ComplaintRequest;
import com.prunny.auth.dto.response.ComplaintResponse;
import com.prunny.auth.model.Complaint;

import java.util.List;

public interface ComplaintService {
    ComplaintResponse createComplaint(ComplaintRequest complaintRequest);
    List<ComplaintResponse> getComplaintsByTenant(Long tenantId);
    List<ComplaintResponse> getComplaintsByProperty(Long propertyId);
}
