package com.example.demo.orders.service;

import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.model.OrderSide;
import com.example.demo.orders.model.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        System.out.println("order mapper created..");
        orderMapper = new OrderMapper();
    }

    @AfterEach
    void tearDown() {
        orderMapper = null;
        System.out.println("order mapper destroyed..");
    }

    @Test
    public void testOrderConversionIsCorrect() {
        System.out.println("testing order conversion from dto");
        OrderDto orderDto = new OrderDto(
                "KCHOL",
                OrderSide.SELL,
                123,
                473.12);
        Order order = orderMapper.toOrder(orderDto);

        compareEquals(orderDto, order);
    }

    @Test
    public void testOrderConversionIsCorrect2() {
        System.out.println("testing order conversion from dto");
        OrderDto orderDto = new OrderDto(
                "KCHOL",
                OrderSide.BUY,
                10,
                473.12);
        Order order = orderMapper.toOrder(orderDto);

        compareEquals(orderDto, order);
    }

    private static void compareEquals(OrderDto orderDto, Order order) {
        assertEquals(orderDto.orderSide(), order.getOrderSide());
        assertEquals(orderDto.price(), order.getPrice());
        assertEquals(orderDto.size(), order.getSize());
        assertEquals(orderDto.assetName(), order.getAssetName());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());

        // check if order-creation date is just before
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 4000)));
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 4000)));
    }

}