package com.SteamCommerce.instruccion_pago.repository;

import com.SteamCommerce.instruccion_pago.entity.InstruccionPagoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstruccionPagoRepository extends JpaRepository<InstruccionPagoEntity, Long> {

    List<InstruccionPagoEntity> findByIdTipoPago(Long idTipoPago);

    List<InstruccionPagoEntity> findByIdTipoPagoAndActivoTrue(Long idTipoPago);

    List<InstruccionPagoEntity> findByActivoTrue();
}
