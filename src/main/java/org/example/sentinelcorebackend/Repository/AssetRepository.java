package org.example.sentinelcorebackend.Repository;

import org.example.sentinelcorebackend.Entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends
        JpaRepository<Asset, Long>,
        JpaSpecificationExecutor<Asset> {

    List<Asset> findByStatus(String status);

    List<Asset> findByAssetType(String assetType);
}