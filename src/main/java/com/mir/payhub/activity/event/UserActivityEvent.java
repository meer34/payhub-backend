package com.mir.payhub.activity.event;

import com.mir.payhub.activity.enums.ActivityType;
import com.mir.payhub.common.entity.BaseEvent;
import com.mir.payhub.profile.entity.Profile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class UserActivityEvent extends BaseEvent {
    private Profile profile;
    private ActivityType type;
    private String title;
    private String description;
    private String status;
    private String referenceId;
}


