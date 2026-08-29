package com.mir.payhub.account.repository;

import com.mir.payhub.account.entity.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, UUID> {
    List<FinancialAccount> findAllByProfileIdOrderByCreatedAtAsc(UUID profileId);
    boolean existsByProfileIdAndCurrency(UUID profileId, String currency);
}
