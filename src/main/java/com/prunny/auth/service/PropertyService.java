package com.prunny.auth.service;

import com.prunny.auth.dto.request.PropertyRequest;
import com.prunny.auth.dto.response.PropertyResponse;
import com.prunny.auth.exception.UnauthorizedException;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;

import java.io.IOException;
import java.util.List;

public interface PropertyService {
    PropertyResponse createProperty(PropertyRequest propertyRequest) throws IOException, UnauthorizedException;
    PropertyResponse updateAvailability(Long propertyId, boolean isAvailable);
    List<PropertyResponse> getAvailableProperties();
    List<PropertyResponse> getAllProperties();
    PropertyResponse getPropertyById(Long propertyId);
    List<PropertyResponse> getPropertiesByLandlord();
    List<PropertyResponse> getPropertyByLandlordId(Long landlordId);
    PropertyResponse updateProperty(Long propertyId, PropertyRequest propertyRequest) throws IOException;
    void deletePropertyById(Long propertyId);
    PropertyResponse approveProperty(Long propertyId) throws UnauthorizedException;
}