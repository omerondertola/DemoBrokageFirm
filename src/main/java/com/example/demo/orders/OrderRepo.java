package com.example.demo.orders;

import com.example.demo.orders.model.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateGreaterThanAndCreateDateLessThan(Long customerId, Date startDate, Date endDate);

    // Locking implemented to prevent
    // lost updates for CANCEL order method,
    // order shall not be full-filled and
    // cancelled
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(Long id);


}
