package com.mir.payhub.activity.controller;

import com.mir.payhub.activity.dto.UserActivityResponse;
import com.mir.payhub.activity.entity.UserActivity;
import com.mir.payhub.activity.service.UserActivityService;
import com.mir.payhub.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;
    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<UserActivityResponse> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(toResponse(
                userActivityService.getById(id)
        ));
    }

    @GetMapping
    public ResponseEntity<Page<UserActivityResponse>> getAll(
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<UserActivityResponse> response =
                userActivityService
                        .getAllByProfileId(
                                profileService.currentProfile().getId(),
                                pageable
                        ).map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        userActivityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserActivityResponse toResponse(UserActivity activity) {
        return UserActivityResponse.builder()
                .id(activity.getId())
                .type(activity.getType())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .status(activity.getStatus())
                .referenceId(activity.getReferenceId())
                .createdAt(activity.getCreatedAt().toLocalDate())
                .build();
    }
}