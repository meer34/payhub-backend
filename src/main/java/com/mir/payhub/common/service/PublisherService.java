package com.mir.payhub.common.service;

import com.mir.payhub.common.entity.BaseEvent;
import com.mir.payhub.profile.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PublisherService {

    private final ProfileService profileService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public PublisherService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public void publishCustomEvent(BaseEvent baseEvent) {
        baseEvent.setProfile(profileService.currentProfile());
        log.info("Publishing PayHub event");
        applicationEventPublisher.publishEvent(baseEvent);
    }
}
