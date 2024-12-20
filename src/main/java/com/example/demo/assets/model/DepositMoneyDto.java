package com.example.demo.assets.model;

import jakarta.validation.constraints.Positive;

public record DepositMoneyDto(

        @Positive(message = "deposit amount must be positive")
        double depositAmount
) {
}
