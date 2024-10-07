package com.example.demo.assets.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AssetDto(
        @NotNull(message = "Asset name cannot be null")
        @NotEmpty(message = "Asset name cannot be emtty")
        String assetName,

        @PositiveOrZero(message = "Customer id must be zero or positive")
        long customerId,

        @PositiveOrZero(message = "Asset size must be zero or positive")
        int assetSize
) {
}
