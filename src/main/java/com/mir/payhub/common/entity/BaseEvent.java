package com.mir.payhub.common.entity;

import com.mir.payhub.profile.entity.Profile;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// Base class for all domain events
// Provides common tracking fields for observability
@Getter
public abstract class BaseEvent {

    private final String eventId;

    // Timestamp when event was created
    private final Instant timestamp;

    // Correlation ID for distributed tracing
    private final String correlationId;

    private Profile profile;

    protected BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.correlationId = null;
    }

    protected BaseEvent(String correlationId) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.correlationId = correlationId;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}