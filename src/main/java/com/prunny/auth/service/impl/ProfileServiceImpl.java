package com.prunny.auth.service.impl;

import com.prunny.auth.dto.request.ProfileRequest;
import com.prunny.auth.dto.response.ProfileResponse;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.model.Profile;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.ProfileRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public ProfileResponse updateProfile(Long userId, ProfileRequest profileRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Profile profile = user.getProfile();
            if (profile == null) {
                profile = new Profile();
                profile.setUser(user);
            }
            profile.setAccountNumber(profileRequest.getAccountNumber());
            profile.setAccountName(profileRequest.getAccountName());
            profile.setBankCode(profileRequest.getBankCode());
            profile.setRecipientCode(profileRequest.getRecipientCode());


             profileRepository.save(profile);

             return new ProfileResponse(profile);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile due to unexpected error", e);
        }
    }
}
