package com.mir.payhub.verification.dto.response;

import com.mir.payhub.verification.enums.VerificationStatus;
import com.mir.payhub.verification.enums.VerificationType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class VerificationResponse {
    private UUID id;
    private VerificationType verificationType;
    private VerificationStatus status;
    private String provider;
    private String providerReference;
}
