package com.mir.payhub.profile.service;

import com.mir.payhub.profile.dto.request.ProfileCreateRequest;
import com.mir.payhub.profile.dto.request.ProfileUpdateRequest;
import com.mir.payhub.profile.dto.response.ProfileResponse;

public interface ProfileService {
    ProfileResponse create(ProfileCreateRequest request);
    ProfileResponse getCurrentProfile();
    ProfileResponse update(ProfileUpdateRequest request);
}
