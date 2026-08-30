package com.mir.payhub.activity.listener;

import com.mir.payhub.activity.entity.UserActivity;
import com.mir.payhub.activity.event.UserActivityEvent;
import com.mir.payhub.activity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityListener {

    private final UserActivityService userActivityService;

    @Async
    @EventListener
    public void handleUserActivityEvent(UserActivityEvent event) {
        log.info("Received UserActivityEvent of type: {} with correlationId: {}", event.getType(), event.getCorrelationId());
        userActivityService.create(toEntity(event));
    }

    private UserActivity toEntity(UserActivityEvent event) {
        return UserActivity.builder()
                .profile(event.getProfile())
                .type(event.getType())
                .title(event.getTitle())
                .description(event.getDescription())
                .status(event.getStatus())
                .referenceId(event.getReferenceId())
                .build();
    }

}
