package com.prunny.auth.service;

import com.prunny.auth.dto.request.ProfileRequest;
import com.prunny.auth.dto.response.ProfileResponse;
import com.prunny.auth.model.Profile;
import com.prunny.auth.model.User;

public interface ProfileService {
    ProfileResponse updateProfile(Long userId, ProfileRequest profileRequest);
}

