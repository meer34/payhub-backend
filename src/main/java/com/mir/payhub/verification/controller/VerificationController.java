package com.mir.payhub.verification.controller;

import com.mir.payhub.verification.dto.response.VerificationResponse;
import com.mir.payhub.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping
    public VerificationResponse get() {
        return verificationService.getCurrentVerification();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VerificationResponse start() {
        return verificationService.startCurrentVerification();
    }
}
