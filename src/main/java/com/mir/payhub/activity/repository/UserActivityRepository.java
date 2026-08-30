package com.mir.payhub.activity.repository;

import com.mir.payhub.activity.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {

    Page<UserActivity> findAllByProfileId(
            UUID profileId,
            Pageable pageable
    );
}