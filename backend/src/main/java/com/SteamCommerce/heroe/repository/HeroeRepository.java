package com.SteamCommerce.heroe.repository;

import com.SteamCommerce.heroe.entity.HeroeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HeroeRepository extends JpaRepository<HeroeEntity, Long> {

    Optional<HeroeEntity> findByNombre(String nombre);

    Optional<HeroeEntity> findByNombreIgnoreCase(String nombre);

    Optional<HeroeEntity> findByNombreInterno(String nombreInterno);

    boolean existsByNombre(String nombre);
}
