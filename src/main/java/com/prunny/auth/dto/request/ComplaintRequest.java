package com.prunny.auth.dto.request;


import lombok.Data;

@Data
public class ComplaintRequest {
    private Long tenantId;
    private Long propertyId;
    private String description;
}
