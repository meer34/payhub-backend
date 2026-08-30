package com.mir.payhub.activity.dto;

import com.mir.payhub.activity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class UserActivityResponse {
    private UUID id;
    private ActivityType type;
    private String title;
    private String description;
    private String status;
    private String referenceId;
    private LocalDate createdAt;
}
