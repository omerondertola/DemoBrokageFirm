package com.example.demo.orders.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST,
        reason = "Order Status is not Valid (i.e. not pending..)")

public class OrderStatusIsNotValidException extends Exception {
}
