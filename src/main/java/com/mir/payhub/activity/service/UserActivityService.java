package com.mir.payhub.activity.service;

import com.mir.payhub.activity.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserActivityService {

    UserActivity create(UserActivity activity);

    UserActivity getById(UUID id);

    Page<UserActivity> getAllByProfileId(UUID profileId, Pageable pageable);

//    UserActivity update(UUID id, UserActivity activity);

    void delete(UUID id);
}