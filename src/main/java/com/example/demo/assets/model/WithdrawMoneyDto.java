package com.example.demo.assets.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record WithdrawMoneyDto(

        @Positive(message = "witdraw amount must be positive")
        double withdrawAmount,

        @NotNull(message = "iban shall exists")
        @NotEmpty(message = "iban shall not be empty-string")
        @Length(message = "length shall be 26", min = 26, max = 26)
        String iban
) {
}
