package com.mir.payhub.account.dto.response;

import com.mir.payhub.account.enums.AccountStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class FinancialAccountResponse {
    private UUID id;
    private String currency;
    private AccountStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}