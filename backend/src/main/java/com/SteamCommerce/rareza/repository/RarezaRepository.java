package com.SteamCommerce.rareza.repository;

import com.SteamCommerce.rareza.entity.RarezaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RarezaRepository extends JpaRepository<RarezaEntity, Long> {

    Optional<RarezaEntity> findByNombre(String nombre);

    Optional<RarezaEntity> findByNombreIgnoreCase(String nombre);

    Optional<RarezaEntity> findByNombreInterno(String nombreInterno);

    boolean existsByNombre(String nombre);
}
