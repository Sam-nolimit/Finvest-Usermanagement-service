package com.prunny.auth.controller;

import com.prunny.auth.dto.request.PropertyRequest;
import com.prunny.auth.dto.response.PropertyResponse;
import com.prunny.auth.enums.Category;
import com.prunny.auth.enums.Kitchen;
import com.prunny.auth.enums.Role;
import com.prunny.auth.enums.Type;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.exception.UnauthorizedException;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(
            @RequestParam("title") String title,
            @RequestParam("category") Category category,
            @RequestParam("type") Type type,
            @RequestParam("kitchen") Kitchen kitchen,
            @RequestParam("price") BigDecimal price,
            @RequestParam("address") String address,
            @RequestParam("locationDescription") String locationDescription,
            @RequestParam("file") MultipartFile file
    ) throws IOException, UnauthorizedException {

        PropertyRequest propertyRequest = buildPropertyRequest(title, category, type, kitchen, price, address, locationDescription, file);
        PropertyResponse propertyResponse = propertyService.createProperty(propertyRequest);
        return ResponseEntity.status(201).body(propertyResponse);
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<PropertyResponse> updateAvailability(@PathVariable Long id, @RequestParam boolean isAvailable) {
        PropertyResponse propertyResponse = propertyService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(propertyResponse);
    }

    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties() {
        List<PropertyResponse> properties = propertyService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        PropertyResponse propertyResponse = propertyService.getPropertyById(id);
        return ResponseEntity.ok(propertyResponse);
    }

    @GetMapping("/land-lord")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByLandlord() throws UnauthorizedException {
        List<PropertyResponse> propertyResponse = propertyService.getPropertiesByLandlord();
        return ResponseEntity.ok(propertyResponse);
    }

    @GetMapping("/land-lord/{landlordId}")
    public ResponseEntity<List<PropertyResponse>> getPropertyByLandlordId(@PathVariable Long landlordId) throws UnauthorizedException {
        List<PropertyResponse> propertyResponse = propertyService.getPropertyByLandlordId(landlordId);
        return ResponseEntity.ok(propertyResponse);
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        List<PropertyResponse> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("category") Category category,
            @RequestParam("type") Type type,
            @RequestParam("kitchen") Kitchen kitchen,
            @RequestParam("price") BigDecimal price,
            @RequestParam("address") String address,
            @RequestParam("locationDescription") String locationDescription,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        PropertyRequest propertyRequest = buildPropertyRequest(title, category, type, kitchen, price, address, locationDescription, file);
        PropertyResponse propertyResponse = propertyService.updateProperty(id, propertyRequest);
        return ResponseEntity.status(201).body(propertyResponse);
    }


    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deletePropertyById(@PathVariable Long propertyId) {
        propertyService.deletePropertyById(propertyId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{propertyId}/approve")
    public PropertyResponse aprroveProperty(@PathVariable Long propertyId) throws UnauthorizedException {
        return propertyService.approveProperty(propertyId);
    }

    private PropertyRequest buildPropertyRequest(String title, Category category, Type type, Kitchen kitchen, BigDecimal price, String address, String locationDescription, MultipartFile file) {
        PropertyRequest propertyRequest = new PropertyRequest();
        propertyRequest.setTitle(title);
        propertyRequest.setCategory(category);
        propertyRequest.setType(type);
        propertyRequest.setKitchen(kitchen);
        propertyRequest.setPrice(price);
        propertyRequest.setAddress(address);
        propertyRequest.setLocationDescription(locationDescription);
        propertyRequest.setFile(file);
        return propertyRequest;
    }
}
