package com.mir.payhub.verification.service;

import com.mir.payhub.verification.dto.response.VerificationResponse;

public interface VerificationService {
    VerificationResponse getCurrentVerification();
    VerificationResponse startCurrentVerification();
}
