package com.mir.payhub.account.service;

import com.mir.payhub.account.dto.request.CreateFinancialAccountRequest;
import com.mir.payhub.account.dto.response.FinancialAccountResponse;

import java.util.List;

public interface FinancialAccountService {
    FinancialAccountResponse create(CreateFinancialAccountRequest request);
    List<FinancialAccountResponse> getCurrentAccounts();
}
