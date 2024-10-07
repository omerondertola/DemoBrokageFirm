package com.example.demo.orders;

import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.model.OrderSide;
import com.example.demo.orders.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderRepo orderRepo;


    @Test
    public void testContextLoaded() {

    }

    @Test
    public void testOrderControllerWired() {
        assertThat(orderController).isNotNull();
    }

    @Test
    public void testAdminCanGetAllOrders() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                "admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsOndersKCHOLBuyOrder(orders[0]);
        assertEqualsOndersSASABuyOrder(orders[1]);
    }

    private void assertEqualsOndersSASABuyOrder(Order order) {
        assertEquals(2, order.getId());
        assertEquals(1, order.getCustomerId());
        assertEquals(OrderSide.BUY,order.getOrderSide());
        assertEquals(100,order.getSize());
        assertEquals(OrderStatus.PENDING,order.getOrderStatus());
        assertEquals(1.0,order.getPrice());
        assertEquals("SASA",order.getAssetName());
        assertNotNull(order.getCreateDate());
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 1000*60*60)));
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 1000*60*60)));
    }

    private void assertEqualsOndersKCHOLBuyOrder(Order order) {
        assertEquals(1, order.getId());
        assertEquals(1, order.getCustomerId());
        assertEquals(OrderSide.BUY,order.getOrderSide());
        assertEquals(10,order.getSize());
        assertEquals(OrderStatus.PENDING,order.getOrderStatus());
        assertEquals(1.733,order.getPrice());
        assertEquals("KCHOL",order.getAssetName());
        assertNotNull(order.getCreateDate());
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 1000*60*60)));
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 1000*60*60)));

    }

    @Test
    public void testOnderIsForbiddenForAllOrders() {
        ResponseEntity response = restTemplate.withBasicAuth(
                        "omerondertola@gmail.com", "admin")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders",
                        Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAdminCanGetOrdersOfOnder() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                        "admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders/1" +
                                "?start-date=20230101&end-date=20260101",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsOndersKCHOLBuyOrder(orders[0]);
        assertEqualsOndersSASABuyOrder(orders[1]);
    }

    @Test
    public void testOnderCanGetOwnOrders() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                        "omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders/1" +
                                "?start-date=20230101&end-date=20260101",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsOndersKCHOLBuyOrder(orders[0]);
        assertEqualsOndersSASABuyOrder(orders[1]);
    }

    @Test
    public void testAdminCanPlaceAndCancelOrderForOnder() {
        // Place the Order
        Long customerId = 1L;
        OrderDto orderDto = new OrderDto("KCHOL",OrderSide.BUY,100, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                "admin@gmail.com", "admin")
                .postForEntity("http://localhost:" + port + "/apis/v1/orders/1",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);
        assertEqualsOrderDto2SavedOrder(orderDto, savedOrder, customerId);

        // Cancel the Order to increase usableSize
        restTemplate.withBasicAuth(
                        "admin@gmail.com", "admin")
                .delete("http://localhost:" + port + "/apis/v1/orders/1/"
                + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders/1/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order cancelledOrder = cancelResponse.getBody();
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getOrderStatus());

        // clean-up for other tests.
        orderRepo.deleteById(cancelledOrder.getId());
    }

    private static void assertEqualsOrderDto2SavedOrder(OrderDto orderDto, Order savedOrder, Long customerId) {
        assertEquals(orderDto.assetName(), savedOrder.getAssetName());
        assertEquals(orderDto.orderSide(), savedOrder.getOrderSide());
        assertEquals(orderDto.size(), savedOrder.getSize());
        assertEquals(orderDto.price(), savedOrder.getPrice());
        assertEquals(OrderStatus.PENDING, savedOrder.getOrderStatus());
        assertEquals(customerId, savedOrder.getCustomerId());
    }

    @Test
    public void testOnderCanPlaceOrderForHimself() {
        // Place the Order
        Long customerId = 1L;
        OrderDto orderDto = new OrderDto("SASA",OrderSide.BUY,1, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                        "omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" + port + "/apis/v1/orders/1",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);
        assertEqualsOrderDto2SavedOrder(orderDto, savedOrder, customerId);

        // Cancel the Order to increase usableSize
        restTemplate.withBasicAuth(
                        "omerondertola@gmail.com", "onder")
                .delete("http://localhost:" + port + "/apis/v1/orders/1/"
                        + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders/1/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order cancelledOrder = cancelResponse.getBody();
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getOrderStatus());

        // clean-up for other tests.
        orderRepo.deleteById(cancelledOrder.getId());
    }

    @Test
    public void testOnderIsForbiddenToPlaceOrderForDefne() {
        // Place the Order
        Long customerId = 2L;
        OrderDto orderDto = new OrderDto("SASA",OrderSide.BUY,100, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                        "onder@gmail.com", "onder")
                .postForEntity("http://localhost:" + port + "/apis/v1/orders/2",
                        orderDto, Order.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testOnderIsForbiddenToCancelDefnesOrders() {
        // Place the Order AS Admin
        Long customerId = 2L;
        OrderDto orderDto = new OrderDto("KCHOL",OrderSide.BUY,100, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                        "admin@gmail.com", "admin")
                .postForEntity("http://localhost:" + port + "/apis/v1/orders/2",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);

        // Cannot Cancel Order as Onder
        restTemplate.withBasicAuth(
                        "onder@gmail.com", "onder")
                .delete("http://localhost:" + port + "/apis/v1/orders/2/"
                        + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port + "/apis/v1/orders/2/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order pendingOrder = cancelResponse.getBody();
        assertEquals(OrderStatus.PENDING, pendingOrder.getOrderStatus());

        // clean-up for other tests.
        orderRepo.deleteById(savedOrder.getId());
    }

}