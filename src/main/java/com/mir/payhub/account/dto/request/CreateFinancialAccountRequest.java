package com.mir.payhub.account.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFinancialAccountRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a three-letter ISO code")
    private String currency;
}
