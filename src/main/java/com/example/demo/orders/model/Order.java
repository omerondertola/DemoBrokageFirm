package com.example.demo.orders.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "order shall have a customer")
    private long customerId;

    @NotNull(message = "order shall be for an asset")
    @NotEmpty(message = "assetName cannot be omitted")
    private String assetName;

    @NotNull(message = "orderSide must not be null")
    private OrderSide orderSide;

    @PositiveOrZero(message = "number of assets must be positive or zero")
    private int size;

    @PositiveOrZero(message = "asset must have a positive or zero price")
    private double price;

    private OrderStatus orderStatus;

    @NotNull
    private Date createDate;

}
