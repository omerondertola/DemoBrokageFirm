package com.example.demo.orders.service;

import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderMapper {

    public Order toOrder(OrderDto dto) {
        return Order.builder()
                .orderSide(dto.orderSide())
                .assetName(dto.assetName())
                .size(dto.size())
                .price(dto.price())
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();
    }
}
