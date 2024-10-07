package com.example.demo.assets;

import com.example.demo.assets.model.Asset;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Long> {

    @Transactional
    List<Asset> findAllByCustomerId(long customerId);

    // Locking implemented to prevent
    // concurrent updates to Asset usable size
    // field.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId and a.assetName = :name")
    Optional<Asset> findByCustomerIdAndAssetName(long customerId, String name);
}
