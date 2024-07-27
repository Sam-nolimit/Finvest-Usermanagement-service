package com.prunny.auth.service.impl;

import com.prunny.auth.config.SecurityConfig;
import com.prunny.auth.enums.Role;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.exception.UnauthorizedException;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.Save;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.SaveRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SaveServiceImpl implements SaveService {

    @Autowired
    private SaveRepository saveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public Save saveProperty(Long propertyId) {
        String email = SecurityConfig.getAuthenticatedUserEmail();
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }
        Optional<Property> property = propertyRepository.findById(propertyId);
        if (!property.isPresent()) {
            throw new ResourceNotFoundException("Property not found");
        }
        try {
            if (!saveRepository.existsByUserAndProperty(user.get(), property.get())) {
                Save save = new Save();
                save.setUser(user.get());
                save.setProperty(property.get());
                return saveRepository.save(save);
            }
            return null;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save property due to unexpected error", e);
        }
    }

    @Override
    @Transactional
    public void unSaveProperty(Long propertyId) {
        String email = SecurityConfig.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        try {
            saveRepository.deleteByUserIdAndPropertyId(user.getId(), propertyId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unsave property due to unexpected error", e);
        }
    }

    @Override
    public List<Save> getSaveByPropertyId(Long propertyId) {
        try {
            return saveRepository.findByPropertyId(propertyId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get saves by property ID due to unexpected error", e);
        }
    }

    @Override
    public List<Save> getSavedPropertiesByUser() {
        String email = SecurityConfig.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        try {
            return saveRepository.findByUserId(user.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get saved properties by user due to unexpected error", e);
        }
    }
}
