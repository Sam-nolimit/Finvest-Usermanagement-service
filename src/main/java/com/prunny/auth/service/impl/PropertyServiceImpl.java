package com.prunny.auth.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.prunny.auth.config.SecurityConfig;
import com.prunny.auth.dto.EmailDetails;
import com.prunny.auth.dto.request.PropertyRequest;
import com.prunny.auth.dto.response.PropertyResponse;
import com.prunny.auth.enums.Role;
import com.prunny.auth.exception.CustomNotFoundException;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.exception.UnauthorizedException;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.EmailService;
import com.prunny.auth.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private EmailService emailService;
    @Autowired
    private SecurityConfig securityConfig;

    @Override
    public PropertyResponse createProperty(PropertyRequest propertyRequest) throws  UnauthorizedException {
        String email = SecurityConfig.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.LANDLORD) {
            throw new UnauthorizedException("User is not authorized to create a property");
        }

        if (propertyRequest.getFile() == null || propertyRequest.getFile().isEmpty()) {
            throw new CustomNotFoundException("File is empty or missing");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(propertyRequest.getFile().getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            Property property = new Property();
            property.setTitle(propertyRequest.getTitle());
            property.setCategory(propertyRequest.getCategory());
            property.setType(propertyRequest.getType());
            property.setKitchen(propertyRequest.getKitchen());
            property.setPrice(propertyRequest.getPrice());
            property.setAddress(propertyRequest.getAddress());
            property.setLocationDescription(propertyRequest.getLocationDescription());
            property.setPhoto(imageUrl);
            property.setLandlord(user);
            property.setAvailable(true);
            property = propertyRepository.save(property);

            Map<String, Object> model = new HashMap<>();
            model.put("propertyTitle", property.getTitle());
            model.put("name", user.getFirstName() + " " + user.getLastName());

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("Property Created Notification: " + property.getTitle())
                    .templateName("registration-email-template")
                    .model(model)
                    .build();
//            emailService.sendEmails(emailDetails);

            return new PropertyResponse(property);
        } catch (ResourceNotFoundException | CustomNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create property due to unexpected error", e);
        }
    }

    @Override
    public PropertyResponse approveProperty(Long propertyId) throws UnauthorizedException {
        try {
            String email = securityConfig.getAuthenticatedUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRole() != Role.ADMIN) {
                throw new UnauthorizedException("User is not authorized to approve the property");
            }

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

            property.setApproved(true);
            propertyRepository.save(property);

            return new PropertyResponse(property);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to approve property due to unexpected error", e);
        }
    }


    @Override
    public PropertyResponse updateAvailability(Long propertyId, boolean isAvailable) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            property.setAvailable(isAvailable);
            property = propertyRepository.save(property);
            return new PropertyResponse(property);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update property availability due to unexpected error", e);
        }
    }

    @Override
    public List<PropertyResponse> getAvailableProperties() {
        try {
            List<Property> properties = propertyRepository.findByIsAvailable(true);
            return properties.stream().map(PropertyResponse::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch available properties due to unexpected error", e);
        }
    }

    @Override
    public List<PropertyResponse> getAllProperties() {
        try {
            List<Property> properties = propertyRepository.findAll();
            return properties.stream().map(PropertyResponse::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all properties due to unexpected error", e);
        }
    }

    @Override
    public PropertyResponse getPropertyById(Long propertyId) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            return new PropertyResponse(property);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch property due to unexpected error", e);
        }
    }

    @Override
    public List<PropertyResponse> getPropertyByLandlordId(Long landlordId) {
        try {
            User user = userRepository.findById(landlordId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRole() != Role.ADMIN && user.getRole() != Role.LANDLORD) {
                throw new UnauthorizedException("User is not authorized to see  properties");
            }
            List<Property> properties = propertyRepository.findByLandlord(user);
            return properties.stream().map(PropertyResponse::new).collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch property due to unexpected error", e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<PropertyResponse> getPropertiesByLandlord() {
        try {
            String email = SecurityConfig.getAuthenticatedUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRole() != Role.ADMIN && user.getRole() != Role.LANDLORD) {
                throw new UnauthorizedException("User is not authorized to see  properties");
            }
            List<Property> properties = propertyRepository.findByLandlord(user);
            return properties.stream().map(PropertyResponse::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch properties by landlord due to unexpected error", e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PropertyResponse updateProperty(Long propertyId, PropertyRequest propertyRequest)  {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

            if (propertyRequest.getFile() != null && !propertyRequest.getFile().isEmpty()) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(propertyRequest.getFile().getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                property.setPhoto(imageUrl);
            }

            property.setTitle(propertyRequest.getTitle());
            property.setCategory(propertyRequest.getCategory());
            property.setType(propertyRequest.getType());
            property.setKitchen(propertyRequest.getKitchen());
            property.setPrice(propertyRequest.getPrice());
            property.setAddress(propertyRequest.getAddress());
            property.setLocationDescription(propertyRequest.getLocationDescription());
            property = propertyRepository.save(property);

            return new PropertyResponse(property);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update property due to unexpected error", e);
        }
    }

    @Override
    public void deletePropertyById(Long propertyId) {
        try {
            if (!propertyRepository.existsById(propertyId)) {
                throw new ResourceNotFoundException("Property not found");
            }
            propertyRepository.deleteById(propertyId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete property due to unexpected error", e);
        }
    }
}
