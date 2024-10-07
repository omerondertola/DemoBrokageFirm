package com.example.demo.orders.service;

import com.example.demo.assets.*;
import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.AssetNames;
import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.MissingAssetException;
import com.example.demo.assets.service.NotEnoughAssetException;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.customers.service.CustomerNotFoundException;
import com.example.demo.orders.OrderRepo;
import com.example.demo.orders.model.Order;
import com.example.demo.orders.model.OrderDto;
import com.example.demo.orders.model.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final AssetRepo assetRepo;
    private final OrderMapper orderMapper;

    @Transactional
    public Order createOrder(long customerId, OrderDto dto) throws AssetNotFoundException, NotEnoughMoneyException, NotEnoughAssetException {
        Order order = orderMapper.toOrder(dto);
        order.setCustomerId(customerId);
        return createOrder(order);
    }

    @Transactional
    public Order createOrder(Order order) throws NotEnoughMoneyException, AssetNotFoundException, NotEnoughAssetException {
        return switch (order.getOrderSide()) {
            case BUY -> processBuyOrder(order);
            case SELL -> processSellOrder(order);
        };

    }

    private Order processBuyOrder(Order order) throws NotEnoughMoneyException, AssetNotFoundException {
        // Get and Lock TRY Asset of the Customer
        // We do not want conflicting updates such
        // as lost updates.
        Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        double cost = order.getSize() * order.getPrice();
        // if the amount of money is enough,
        // then record the order and decrease
        // usable amount.
        if (tryAsset.getUsableSize() >= cost) {
            tryAsset.setUsableSize(tryAsset.getUsableSize() - cost);
            assetRepo.save(tryAsset);
            // defensive coding, ensure order is pending..
            order.setOrderStatus(OrderStatus.PENDING);
            return orderRepo.save(order);
        } else {
            // If TRY asset does not have enough money
            // then throw an exception.
            throw new NotEnoughMoneyException();
        }
    }

    private Order processSellOrder(Order order) throws AssetNotFoundException, NotEnoughAssetException {
        // Find and Lock the corresponding asset to SELL.
        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                order.getAssetName()
        ).orElseThrow(AssetNotFoundException::new);

        // Check if the amount of the asset is enough
        if (asset.getUsableSize() >= order.getSize()) {
            // Decrease the number of usable assets & Update
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            assetRepo.save(asset);
            // Defensive code, order status shall
            // be pending while creating..
            order.setOrderStatus(OrderStatus.PENDING);
            // Create the order.
            return orderRepo.save(order);
        } else {
            throw new NotEnoughAssetException();
        }
    }

    @Transactional
    public List<Order> findAllBetween(Long customerId, Date startDate, Date endDate) {
        return orderRepo.findByCustomerIdAndCreateDateGreaterThanAndCreateDateLessThan(
                customerId, startDate, endDate
        );
    }

    @Transactional
    public Order cancelOrder(
            long orderId,
            long customerId) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException, CustomerNotFoundException {
        // Find & Lock the Order - We do not want the order
        // to be processed by any other party for instance
        // to execute the order.
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(order.getCustomerId() != customerId) {
            throw new CustomerNotFoundException();
        }

        // Check if the order is pending.
        // If the order is not in pending state, then it
        // shall not be processed.
        if(!OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new OrderStatusIsNotValidException();
        }

        return switch (order.getOrderSide()) {
            case BUY -> processCancelBuyOrder(order);
            case SELL -> processCancelSellOrder(order);
        };
    }

    private Order processCancelSellOrder(Order order) throws AssetNotFoundException {
        // If Sell, find the ASSET of user to SELL
        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                order.getAssetName()
        ).orElseThrow(AssetNotFoundException::new);

        // Increment the usableSize of the ASSET
        asset.setUsableSize(asset.getUsableSize() + order.getSize());
        assetRepo.save(asset);

        // Mark the order as CANCELLED.
        order.setOrderStatus(OrderStatus.CANCELLED);

        // Save the order.
        return orderRepo.save(order);
    }

    private Order processCancelBuyOrder(Order order) throws AssetNotFoundException {
        // If Buy, lock user-TRY asset & update accordingly
        Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        // If Buy, increment usableSize of TRY asset
        double cost = order.getPrice() * order.getSize();

        tryAsset.setUsableSize(tryAsset.getUsableSize() + cost);
        assetRepo.save(tryAsset);

        // Mark the order as CANCELLED.
        order.setOrderStatus(OrderStatus.CANCELLED);
        // Save the order.
        return orderRepo.save(order);
    }

    public List<Order> findAll() {
        return orderRepo.findAll();
    }

    public Optional<Order> findOrder(Long customerId, Long orderId) {
        return orderRepo.findById(orderId);
    }

    @Transactional
    public Order matchOrder(long orderId) throws OrderNotFoundException, AssetNotFoundException {
        // Order of locking the Database entities is necessary
        // to prevent Deadlocks.
        // We first lock the order, then lock the TRY asset
        // for consistency purposes with the Cancel order
        // workflow.

        // ORDER OF LOCKS:
        // 1. ORDER
        // 2. TRY ASSET
        // 3. CUSTOMER TO SYNCH ON NEW-ASSET CREATION
        // 4. ASSET TO BUY
        Order order = orderRepo.findByIdForUpdate(orderId).orElseThrow(
                OrderNotFoundException::new);

        return switch (order.getOrderSide()) {
            case BUY -> processMatchBuyOrder(order);
            case SELL -> processMatchSellOrder(order);
        };
    }

    private Order processMatchBuyOrder(Order order) throws AssetNotFoundException {
        // Get and Lock TRY Asset of the Customer
        // We do not want conflicting updates such
        // as lost updates.
        Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        // update TRY asset to reflect BUY operation
        double cost = order.getSize() * order.getPrice();
        tryAsset.setSize(tryAsset.getSize() - cost);
        assetRepo.save(tryAsset);

        // UPDATE or CREATE ASSET to BUY
        updateOrCreateAssetToBuy(order);

        order.setOrderStatus(OrderStatus.MATCHED);
        return orderRepo.save(order);
    }

    private void updateOrCreateAssetToBuy(Order order) {
        Optional<Asset> optAssetToBuy = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(), order.getAssetName()
        );
        if(optAssetToBuy.isPresent()) {
            // Update Size & Usable Size of the Asset
            Asset assetToBuy = optAssetToBuy.get();
            assetToBuy.setSize(assetToBuy.getSize() + order.getSize());
            assetToBuy.setUsableSize(assetToBuy.getUsableSize() + order.getSize());
            assetRepo.save(assetToBuy);

        } else {
            // We have unique constraint for customerId, assetName
            // If two admin performs matching for a non-existent
            // asset at the same time, one of them will fail.

            // Create a new Asset
            Asset newAsset = Asset.builder()
                    .assetName(order.getAssetName())
                    .size(order.getSize())
                    .usableSize(order.getSize())
                    .customerId(order.getCustomerId())
                    .build();
            assetRepo.save(newAsset);
        }
    }

    private Order processMatchSellOrder(Order order) throws AssetNotFoundException {
        // Get and Lock TRY Asset of the Customer
        // We do not want conflicting updates such
        // as lost updates.
        Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        // update TRY asset to reflect SELL operation
        double cost = order.getSize() * order.getPrice();
        tryAsset.setSize(tryAsset.getSize() + cost);
        tryAsset.setUsableSize(tryAsset.getUsableSize() + cost);
        assetRepo.save(tryAsset);

        // UPDATE ASSET TO SELL
        updateAssetToSell(order);

        order.setOrderStatus(OrderStatus.MATCHED);
        return orderRepo.save(order);
    }

    private void updateAssetToSell(Order order) throws AssetNotFoundException {
        Optional<Asset> optAssetToSell = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(), order.getAssetName()
        );
        if(optAssetToSell.isPresent()) {
            // Update Size & Usable Size of the
            // Asset to reflect SELL operation
            Asset assetToSell = optAssetToSell.get();
            assetToSell.setSize(assetToSell.getSize() - order.getSize());
            assetRepo.save(assetToSell);
        } else {
            // Inconsistent state detected.
            System.err.println("Inconsistent state is detected.");
            System.err.println("Asset not exists for which a pending SELL order is present.");
            System.err.println("Details of the related order: "+ order);
            throw new MissingAssetException();
        }
    }
}
