package com.prunny.auth.dto.response;

import com.prunny.auth.model.Complaint;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplaintResponse {
    private Long id;
    private Long tenantId;
    private Long propertyId;
    private String description;
    private LocalDateTime createdAt;

    public ComplaintResponse(Complaint complaint) {
        this.id = complaint.getId();
        this.tenantId = complaint.getTenant().getId();
        this.propertyId = complaint.getProperty().getId();
        this.description = complaint.getDescription();
        this.createdAt = complaint.getCreatedAt();
    }
}
