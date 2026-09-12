package com.SteamCommerce.item.repository;

import com.SteamCommerce.item.entity.ItemEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    Optional<ItemEntity> findByAssetId(String assetId);

    boolean existsByAssetId(String assetId);

    List<ItemEntity> findByMarketHashName(String marketHashName);

    @Query("SELECT i FROM ItemEntity i WHERE i.assetId IN :assetIds")
    List<ItemEntity> findAllByAssetIdIn(@Param("assetIds") Collection<String> assetIds);
}
