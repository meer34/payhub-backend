package com.mir.payhub.account.service.impl;

import com.mir.payhub.account.dto.request.CreateFinancialAccountRequest;
import com.mir.payhub.account.dto.response.FinancialAccountResponse;
import com.mir.payhub.account.entity.FinancialAccount;
import com.mir.payhub.account.enums.AccountStatus;
import com.mir.payhub.account.repository.FinancialAccountRepository;
import com.mir.payhub.account.service.FinancialAccountService;
import com.mir.payhub.activity.enums.ActivityType;
import com.mir.payhub.activity.event.UserActivityEvent;
import com.mir.payhub.common.service.PublisherService;
import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FinancialAccountServiceImpl implements FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final ProfileService profileService;
    private final PublisherService publisherService;

    @Override
    @Transactional
    public FinancialAccountResponse create(CreateFinancialAccountRequest request) {

        Profile profile = profileService.currentProfile();

        String currency = request.getCurrency()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (financialAccountRepository.existsByProfileIdAndCurrency(
                profile.getId(), currency)) {

            publisherService.publishCustomEvent(
                    UserActivityEvent.builder()
                            .type(ActivityType.ACCOUNT_CREATION_FAILED)
                            .title("Account Creation Failed")
                            .description("An account already exists for this currency")
                            .status("FAILED")
                            .referenceId(null)
                            .build()
            );

            throw new BadRequestException(
                    "An account already exists for this currency"
            );
        }

        FinancialAccount account = FinancialAccount.builder()
                .profile(profile)
                .currency(currency)
                .status(AccountStatus.ACTIVE)
                .build();

        FinancialAccount savedAccount =
                financialAccountRepository.save(account);

        publisherService.publishCustomEvent(
                UserActivityEvent.builder()
                        .type(ActivityType.ACCOUNT_CREATED)
                        .title("Account Created")
                        .description(
                                "A " + currency + " account was created successfully"
                        )
                        .status("SUCCESS")
                        .referenceId(savedAccount.getId().toString())
                        .build()
        );

        return toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialAccountResponse> getCurrentAccounts() {

        return financialAccountRepository
                .findAllByProfileIdOrderByCreatedAtAsc(profileService.currentProfile().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FinancialAccountResponse toResponse(
            FinancialAccount account) {

        return FinancialAccountResponse.builder()
                .id(account.getId())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}