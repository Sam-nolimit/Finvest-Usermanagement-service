package com.prunny.auth.controller;

import com.prunny.auth.model.Save;
import com.prunny.auth.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class SaveController {
    private final SaveService saveService;

    @PostMapping("/save/{propertyId}")
    public ResponseEntity<Save> saveProperty(@PathVariable Long propertyId) {
        Save savedProperty = saveService.saveProperty(propertyId);
        if (savedProperty == null) {
            return ResponseEntity.badRequest().build(); // If the property is already saved
        }
        return ResponseEntity.ok(savedProperty);
    }

    @PostMapping("/unsave/{propertyId}")
    public ResponseEntity<Void> unSaveProperty( @PathVariable Long propertyId) {
        saveService.unSaveProperty( propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/save/property/{propertyId}")
    public ResponseEntity<List<Save>> getSavedByPropertyId(@PathVariable Long propertyId) {
        List<Save> savedProperties = saveService.getSaveByPropertyId(propertyId);
        return ResponseEntity.ok(savedProperties);
    }

//    @GetMapping("/save/{userId}")
//    public ResponseEntity<List<Save>> getSavedPropertiesByUser(@PathVariable Long userId) {
//        List<Save> savedProperties = saveService.getSavedPropertiesByUser(userId);
//        return ResponseEntity.ok(savedProperties);
//    }
    @GetMapping("/save")
    public ResponseEntity<List<Save>> getSavedPropertiesByUser() {
        List<Save> savedProperties = saveService.getSavedPropertiesByUser();
        return ResponseEntity.ok(savedProperties);
    }
}
