package com.mir.payhub.account.controller;

import com.mir.payhub.account.dto.request.CreateFinancialAccountRequest;
import com.mir.payhub.account.dto.response.FinancialAccountResponse;
import com.mir.payhub.account.service.FinancialAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class FinancialAccountController {

    private final FinancialAccountService financialAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialAccountResponse create(@Valid @RequestBody CreateFinancialAccountRequest request) {
        return financialAccountService.create(request);
    }

    @GetMapping
    public List<FinancialAccountResponse> getAll() {
        return financialAccountService.getCurrentAccounts();
    }
}
