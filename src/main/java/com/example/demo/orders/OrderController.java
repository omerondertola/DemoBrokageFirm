package com.example.demo.orders;

import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.NotEnoughAssetException;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.customers.service.CustomerNotFoundException;
import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.service.OrderNotFoundException;
import com.example.demo.orders.service.OrderService;
import com.example.demo.orders.service.OrderStatusIsNotValidException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1/orders")
public class OrderController {

    private final OrderService orderService;


    // TODO - LIST ALL ORDERS REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }


    // TODO - GET ORDERS BY DATE REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByDate(
            @PathVariable("customerId") Long customerId,

            @DateTimeFormat(pattern="yyyyMMdd")
            @RequestParam(name="start-date")
            Date startDate,

            @DateTimeFormat(pattern="yyyyMMdd")
            @RequestParam(name="end-date", required = true)
            Date endDate
    ) {
        return ResponseEntity.ok(
                orderService.findAllBetween(customerId, startDate, endDate)
        );
    }

    // TODO - GET ORDERS BY DATE REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable("customerId") Long customerId,

            @PathVariable("orderId") Long orderId
            ) {
        return orderService.findOrder(customerId, orderId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }


    // TODO - CREATE ORDER REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @PostMapping("/{customerId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable(name = "customerId")
            long customerId,

            @Valid @RequestBody
            OrderDto order
    ) throws AssetNotFoundException, NotEnoughMoneyException, NotEnoughAssetException {
        return ResponseEntity.ok(
                orderService.createOrder(customerId, order)
        );
    }


    // TODO - DELETE ORDER REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @DeleteMapping("/{customerId}/{orderId}")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable("customerId")
            long customerId,

            @PathVariable("orderId")
            long orderId
    ) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException,
            CustomerNotFoundException {
        return ResponseEntity.ok(orderService.cancelOrder(orderId,customerId));
    }

    // TODO - DELETE ORDER REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/match/{orderId}")
    public ResponseEntity<Order> matchOrder(
            @PathVariable("orderId")
            long orderId
    ) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException {
        return ResponseEntity.ok(orderService.matchOrder(orderId));
    }

}
