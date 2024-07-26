package com.prunny.auth.controller;

import com.prunny.auth.dto.request.ProfileRequest;
import com.prunny.auth.dto.response.ProfileResponse;
import com.prunny.auth.model.Profile;
import com.prunny.auth.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable Long userId, @RequestBody ProfileRequest profileRequest) {
        ProfileResponse updatedProfile = profileService.updateProfile(userId, profileRequest);
        return ResponseEntity.ok(updatedProfile);
    }
}
