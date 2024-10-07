package com.example.demo.orders.service;

import com.example.demo.assets.AssetRepo;
import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.AssetNames;
import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.NotEnoughAssetException;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.customers.service.CustomerNotFoundException;
import com.example.demo.orders.OrderRepo;
import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.model.OrderSide;
import com.example.demo.orders.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepo orderRepo;

    @Mock
    AssetRepo assetRepo;

    @Mock
    OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShallCreateBuyOrder() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.BUY, 100, 1.736);

        Order buyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(100_000)
                .size(100_000)
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(100_000)
                .usableSize(100_000 - 1.736*100)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(buyKCHOL))
                .thenReturn(resultBuyKCHOL);

        //;
        //return orderRepo.save(order);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception");
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
        } catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedTryAsset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(buyKCHOL.getId(), returnValue.getId());
        assertEquals(buyKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyKCHOL.getSize(), returnValue.getSize());
        assertEquals(buyKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.PENDING, returnValue.getOrderStatus());
        assertEquals(buyKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallCreateSellOrder() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.SELL, 100, 1.736);

        Order sellKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset kcholAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(150)
                .size(150)
                .build();

        Asset savedKCHOLAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(50)
                .size(150)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "KCHOL"
        )).thenReturn(Optional.of(kcholAsset));

        Mockito.when(assetRepo.save(kcholAsset))
                .thenReturn(savedKCHOLAsset);

        Mockito.when(orderRepo.save(sellKCHOL))
                .thenReturn(resultSELLKCHOL);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception");
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
        } catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedKCHOLAsset.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(returnValue);
        assertEquals(sellKCHOL.getId(), returnValue.getId());
        assertEquals(sellKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellKCHOL.getSize(), returnValue.getSize());
        assertEquals(sellKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.PENDING, returnValue.getOrderStatus());
        assertEquals(sellKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowNotEnoughAssetException() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.SELL, 11, 1.736);

        Order sellKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset kcholAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(10)
                .size(150)
                .build();

        Asset savedKCHOLAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(10)
                .size(150)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "KCHOL"
        )).thenReturn(Optional.of(kcholAsset));

        Mockito.when(assetRepo.save(kcholAsset))
                .thenReturn(savedKCHOLAsset);

        Mockito.when(orderRepo.save(sellKCHOL))
                .thenReturn(resultSELLKCHOL);

        // When

        var msg = assertThrows(NotEnoughAssetException.class,
                () -> orderService.createOrder(customerId, orderDto),
                "should have thrown NotEnoughAssetException");
        assertEquals("Not Enough usableSize(ASSET)", msg.getMessage());
    }

    @Test
    public void testShallNotThrowNotEnoughAssetExceptionOnBoundary() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.SELL, 10, 1.736);

        Order sellKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(10)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(10)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset kcholAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(10)
                .size(150)
                .build();

        Asset savedKCHOLAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(0)
                .size(150)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "KCHOL"
        )).thenReturn(Optional.of(kcholAsset));

        Mockito.when(assetRepo.save(kcholAsset))
                .thenReturn(savedKCHOLAsset);

        Mockito.when(orderRepo.save(sellKCHOL))
                .thenReturn(resultSELLKCHOL);

        // When

        boolean notEnoughAssetExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception");
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
        }catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedKCHOLAsset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(sellKCHOL.getId(), returnValue.getId());
        assertEquals(sellKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellKCHOL.getSize(), returnValue.getSize());
        assertEquals(sellKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.PENDING, returnValue.getOrderStatus());
        assertEquals(sellKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenSo() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.BUY, 100_000, 1.736);

        Order buyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(100_000)
                .size(100_000)
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(100_000)
                .usableSize(100_000 - 1.736*100_000)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(buyKCHOL))
                .thenReturn(resultBuyKCHOL);

        //;
        //return orderRepo.save(order);

        // When

        boolean notEnoughMoneyExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception");
        } catch (NotEnoughMoneyException e) {
            notEnoughMoneyExcThrown = true;
        }catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        // Then
        assertNull(returnValue);
        assertTrue(notEnoughMoneyExcThrown);
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenJustEqual() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.BUY, 100_000, 1);

        Order buyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(100_000)
                .size(100_000)
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(100_000)
                .usableSize(100_000 - 1.0*100_000)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(buyKCHOL))
                .thenReturn(resultBuyKCHOL);

        //;
        //return orderRepo.save(order);

        // When

        boolean notEnoughMoneyExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception");
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception.");
        }catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        // Then
        assertNotNull(returnValue);
        assertEquals(buyKCHOL.getId(), returnValue.getId());
        assertEquals(buyKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyKCHOL.getSize(), returnValue.getSize());
        assertEquals(buyKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.PENDING, returnValue.getOrderStatus());
        assertEquals(buyKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowAssetNotFoundExceptionWhenSo() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.BUY, 100_000, 1.736);

        Order buyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100_000)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.empty());

        //;
        //return orderRepo.save(order);

        // When

        boolean assetNotFoundExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            assetNotFoundExcThrown = true;
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception.");
        }catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        // Then
        assertNull(returnValue);
        assertTrue(assetNotFoundExcThrown);
    }

    @Test
    public void testCanCancelBuyOrder() {
        // Given
        var customerId = 1L;

        Order buyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.CANCELLED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(100_000)
                .usableSize(100_000 - 1.736*100)
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(100_000)
                .usableSize(100_000)
                .build();


        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(buyKCHOL.getId()))
                .thenReturn(Optional.of(buyKCHOL));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(buyKCHOL))
                .thenReturn(resultBuyKCHOL);

        //;
        //return orderRepo.save(order);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.cancelOrder(buyKCHOL.getId(), customerId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw NotEnoughAssetException");
        } catch (OrderStatusIsNotValidException e) {
            fail("shall not throw OrderStatusIsNotValidException");
        } catch (CustomerNotFoundException e) {
            fail("shall not throw CustomerNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedTryAsset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(buyKCHOL.getId(), returnValue.getId());
        assertEquals(buyKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyKCHOL.getSize(), returnValue.getSize());
        assertEquals(buyKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.CANCELLED, returnValue.getOrderStatus());
        assertEquals(buyKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testCanCancelSellOrder() {

        // TODO - Cancel Sell Order.

        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("KCHOL", OrderSide.SELL, 100, 1.736);

        Order sellKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLKCHOL = Order.builder()
                .assetName("KCHOL")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(1.736)
                .id(2)
                .customerId(customerId)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset kcholAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(150)
                .size(150)
                .build();

        Asset savedKCHOLAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .usableSize(50)
                .size(150)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "KCHOL"
        )).thenReturn(Optional.of(kcholAsset));

        Mockito.when(assetRepo.save(kcholAsset))
                .thenReturn(savedKCHOLAsset);

        Mockito.when(orderRepo.save(sellKCHOL))
                .thenReturn(resultSELLKCHOL);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw NotEnoughMoneyException");
        } catch (NotEnoughAssetException e) {
            fail("shall not throw NotEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedKCHOLAsset.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(returnValue);
        assertEquals(sellKCHOL.getId(), returnValue.getId());
        assertEquals(sellKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellKCHOL.getSize(), returnValue.getSize());
        assertEquals(sellKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.PENDING, returnValue.getOrderStatus());
        assertEquals(sellKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallMatchBuyOrders() {
        // Given
        // We have TWO Pending Orders in the DB
        // for customer Ömer Önder TOLA
        // One for KCHOL other SASA details
        // reflected below.
        var customerId = 1L;
        var orderId = 1L;

        Order buyKCHOL = Order.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(1.733)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order savedBuyKCHOL = Order.builder()
                .id(1)
                .customerId(customerId)
                .assetName("KCHOL")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(1.733)
                .orderStatus(OrderStatus.MATCHED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order buySASA = Order.builder()
                .id(2)
                .customerId(customerId)
                .assetName("SASA")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10_000)
                .usableSize(10_000 - (1.733 * 10) - (1.0 * 100))
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10_000 - (1.733 * 10))
                .usableSize(10_000 - (1.733 * 10) - (1.0 * 100))
                .build();

        Asset newKCHOLAsset = Asset.builder()
                .customerId(customerId)
                .assetName("KCHOL")
                .size(10)
                .usableSize(10)
                .build();

        Asset savedKCHOLAsset = Asset.builder()
                .id(5)
                .customerId(customerId)
                .assetName("KCHOL")
                .size(10)
                .usableSize(10)
                .build();

        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(orderId))
                .thenReturn(Optional.of(buyKCHOL));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,  AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(buyKCHOL))
                .thenReturn(savedBuyKCHOL);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                buySASA.getCustomerId(), buyKCHOL.getAssetName()
        )).thenReturn(Optional.empty());

        Mockito.when(assetRepo.save(newKCHOLAsset))
                .thenReturn(savedKCHOLAsset);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.matchOrder(orderId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw OrderNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        verify(assetRepo, times(1)).save(tryAsset);
        verify(assetRepo, times(1)).save(newKCHOLAsset);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo, times(2)).save(assetCaptor.capture());
        List<Asset> allValues = assetCaptor.getAllValues();
        assertEquals(2, allValues.size());

        // Assert that TRY asset is updated correctly.
        Asset capturedTryAsset = allValues.get(0);
        assertEquals(savedTryAsset.getSize(), capturedTryAsset.getSize());
        assertEquals(savedTryAsset.getUsableSize(), capturedTryAsset.getUsableSize());

        // Assert that KCHOL Asset is created correctly.
        Asset capturedKCHOLAsset = allValues.get(1);
        assertEquals(savedKCHOLAsset.getSize(), capturedKCHOLAsset.getSize());
        assertEquals(savedKCHOLAsset.getUsableSize(), capturedKCHOLAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(savedBuyKCHOL.getId(), returnValue.getId());
        assertEquals(savedBuyKCHOL.getOrderSide(), returnValue.getOrderSide());
        assertEquals(savedBuyKCHOL.getSize(), returnValue.getSize());
        assertEquals(savedBuyKCHOL.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.MATCHED, returnValue.getOrderStatus());
        assertEquals(savedBuyKCHOL.getCreateDate(), returnValue.getCreateDate());
        assertEquals(savedBuyKCHOL.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallMatchSellOrders() {
        // Given
        // ISYAT ASSET FOR OMER ONDER TOLA
        // DETAILS GIVEN BELOW
        var customerId = 1L;
        var orderId = 3L;

        Order sellISYAT = Order.builder()
                .id(3)
                .customerId(customerId)
                .assetName("ISYAT")
                .orderSide(OrderSide.SELL)
                .size(1_000)
                .price(9.35)
                .orderStatus(OrderStatus.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order savedSellISYAT = Order.builder()
                .id(3)
                .customerId(customerId)
                .assetName("ISYAT")
                .orderSide(OrderSide.SELL)
                .size(1_000)
                .price(9.35)
                .orderStatus(OrderStatus.MATCHED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10_000)
                .usableSize(10_000 - (1.733 * 10) - (1.0 * 100))
                .build();

        Asset savedTryAsset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10_000 + (9.35 * 1_000))
                .usableSize(10_000 - (1.733 * 10) - (1.0 * 100) + (9.35 * 1_000))
                .build();

        Asset isyatAsset = Asset.builder()
                .id(2L)
                .customerId(customerId)
                .assetName("ISYAT")
                .size(10_000)
                .usableSize(9_000)
                .build();

        Asset savedISYATAsset = Asset.builder()
                .id(2L)
                .customerId(customerId)
                .assetName("ISYAT")
                .size(9_000)
                .usableSize(9_000)
                .build();

        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(orderId))
                .thenReturn(Optional.of(sellISYAT));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        customerId,  AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(tryAsset))
                .thenReturn(savedTryAsset);

        Mockito.when(orderRepo.save(sellISYAT))
                .thenReturn(savedSellISYAT);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                sellISYAT.getCustomerId(), sellISYAT.getAssetName()
        )).thenReturn(Optional.of(isyatAsset));

        Mockito.when(assetRepo.save(isyatAsset))
                .thenReturn(savedISYATAsset);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.matchOrder(orderId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw OrderNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        verify(assetRepo, times(1)).save(tryAsset);
        verify(assetRepo, times(1)).save(isyatAsset);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo, times(2)).save(assetCaptor.capture());
        List<Asset> allValues = assetCaptor.getAllValues();
        assertEquals(2, allValues.size());

        // Assert that TRY asset is updated correctly.
        Asset capturedTryAsset = allValues.get(0);
        assertEquals(savedTryAsset.getSize(), capturedTryAsset.getSize());
        assertEquals(savedTryAsset.getUsableSize(), capturedTryAsset.getUsableSize());

        // Assert that KCHOL Asset is created correctly.
        Asset capturedISYATAsset = allValues.get(1);
        assertEquals(savedISYATAsset.getSize(), capturedISYATAsset.getSize());
        assertEquals(savedISYATAsset.getUsableSize(), capturedISYATAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(savedSellISYAT.getId(), returnValue.getId());
        assertEquals(savedSellISYAT.getOrderSide(), returnValue.getOrderSide());
        assertEquals(savedSellISYAT.getSize(), returnValue.getSize());
        assertEquals(savedSellISYAT.getPrice(), returnValue.getPrice());
        assertEquals(OrderStatus.MATCHED, returnValue.getOrderStatus());
        assertEquals(savedSellISYAT.getCreateDate(), returnValue.getCreateDate());
        assertEquals(savedSellISYAT.getCustomerId(), returnValue.getCustomerId());
    }

}