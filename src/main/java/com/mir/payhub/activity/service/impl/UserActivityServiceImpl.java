package com.mir.payhub.activity.service.impl;

import com.mir.payhub.activity.entity.UserActivity;
import com.mir.payhub.activity.repository.UserActivityRepository;
import com.mir.payhub.activity.service.UserActivityService;
import com.mir.payhub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;

    @Override
    public UserActivity create(UserActivity activity) {
        return userActivityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserActivity getById(UUID id) {
        return userActivityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User activity not found with id: " + id
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserActivity> getAllByProfileId(
            UUID profileId,
            Pageable pageable) {

        return userActivityRepository.findAllByProfileId(
                profileId,
                pageable
        );
    }

    /*
    @Override
    public UserActivity update(UUID id, UserActivity activity) {
        UserActivity existing = getById(id);

        existing.setType(activity.getType());
        existing.setTitle(activity.getTitle());
        existing.setDescription(activity.getDescription());
        existing.setStatus(activity.getStatus());
        existing.setReferenceId(activity.getReferenceId());

        return userActivityRepository.save(existing);
    }
    */

    @Override
    public void delete(UUID id) {
        UserActivity existing = getById(id);
        userActivityRepository.delete(existing);
    }
}