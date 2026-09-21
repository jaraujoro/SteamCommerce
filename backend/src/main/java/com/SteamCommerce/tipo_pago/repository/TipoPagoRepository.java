package com.SteamCommerce.tipo_pago.repository;

import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPagoEntity, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdTipoPagoNot(String nombre, Long idTipoPago);

}
