package com.mir.payhub.account.service.impl;

import com.mir.payhub.account.dto.request.CreateFinancialAccountRequest;
import com.mir.payhub.account.dto.response.FinancialAccountResponse;
import com.mir.payhub.account.entity.FinancialAccount;
import com.mir.payhub.account.enums.AccountStatus;
import com.mir.payhub.account.repository.FinancialAccountRepository;
import com.mir.payhub.account.service.FinancialAccountService;
import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.exception.ResourceNotFoundException;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.profile.repository.ProfileRepository;
import com.mir.payhub.security.service.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FinancialAccountServiceImpl implements FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public FinancialAccountResponse create(CreateFinancialAccountRequest request) {

        Profile profile = currentProfile();

        String currency = request.getCurrency().toUpperCase(Locale.ROOT);

        if (financialAccountRepository.existsByProfileIdAndCurrency(
                profile.getId(), currency)) {
            throw new BadRequestException(
                    "An account already exists for this currency"
            );
        }

        FinancialAccount account = FinancialAccount.builder()
                .profile(profile)
                .currency(currency)
                .status(AccountStatus.ACTIVE)
                .build();

        return toResponse(financialAccountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialAccountResponse> getCurrentAccounts() {

        return financialAccountRepository
                .findAllByProfileIdOrderByCreatedAtAsc(currentProfile().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Profile currentProfile() {

        Object principal =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        if (!(principal instanceof CustomUserPrincipal customUserPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user principal is unavailable"
            );
        }

        return profileRepository.findByUserId(customUserPrincipal.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));
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