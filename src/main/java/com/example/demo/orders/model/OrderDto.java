package com.example.demo.orders.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderDto(
        @NotNull(message = "order shall be for an asset")
        @NotEmpty(message = "assetName cannot be omitted")
        String assetName,

        OrderSide orderSide,

        @PositiveOrZero(message = "size of the asset must be positive or zero")
        int size,

        @PositiveOrZero(message = "asset must have a positive or zero price")
        double price
) {
}
