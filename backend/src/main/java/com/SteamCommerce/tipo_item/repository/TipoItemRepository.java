package com.SteamCommerce.tipo_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SteamCommerce.tipo_item.entity.TipoItemEntity;

import java.util.Optional;

@Repository
public interface TipoItemRepository extends JpaRepository<TipoItemEntity, Long> {

    Optional<TipoItemEntity> findByNombre(String nombre);

    Optional<TipoItemEntity> findByNombreIgnoreCase(String nombre);

    Optional<TipoItemEntity> findByNombreInterno(String nombreInterno);

    boolean existsByNombre(String nombre);

}
