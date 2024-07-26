package com.prunny.auth.controller;

import com.prunny.auth.dto.request.ComplaintRequest;
import com.prunny.auth.dto.response.ComplaintResponse;
import com.prunny.auth.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(@RequestBody ComplaintRequest complaintRequest) {
        ComplaintResponse newComplaint = complaintService.createComplaint(complaintRequest);
        return ResponseEntity.status(201).body(newComplaint);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByTenant(@PathVariable Long tenantId) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByTenant(tenantId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByProperty(@PathVariable Long propertyId) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByProperty(propertyId);
        return ResponseEntity.ok(complaints);
    }
}
