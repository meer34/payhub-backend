package com.mir.payhub.profile.controller;

import com.mir.payhub.profile.dto.request.ProfileCreateRequest;
import com.mir.payhub.profile.dto.request.ProfileUpdateRequest;
import com.mir.payhub.profile.dto.response.ProfileResponse;
import com.mir.payhub.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@Valid @RequestBody ProfileCreateRequest request) {
        return profileService.create(request);
    }

    @GetMapping
    public ProfileResponse get() {
        log.info("Fetching current profile");
        return profileService.getCurrentProfile();
    }

    @PatchMapping
    public ProfileResponse update(@Valid @RequestBody ProfileUpdateRequest request) {
        return profileService.update(request);
    }
}
