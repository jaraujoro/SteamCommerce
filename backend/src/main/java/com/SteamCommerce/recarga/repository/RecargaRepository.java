package com.SteamCommerce.recarga.repository;

import com.SteamCommerce.recarga.entity.RecargaEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecargaRepository extends JpaRepository<RecargaEntity, Long> {

    boolean existsByCodigoRecarga(String codigoRecarga);

    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    Optional<RecargaEntity> findByCodigoRecarga(String codigoRecarga);

    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    Optional<RecargaEntity> findWithRelacionesByIdRecarga(Long idRecarga);

    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    List<RecargaEntity> findByIdUsuarioOrderByFechaCreacionDesc(UUID idUsuario);

    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    List<RecargaEntity> findByEstadoIgnoreCaseOrderByFechaCreacionDesc(String estado);

    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    List<RecargaEntity> findAllByOrderByFechaCreacionDesc();

    // buscar la recarga PENDIENTE que coincida en monto y tipo de pago
    @EntityGraph(attributePaths = { "usuario", "tipoPago" })
    Optional<RecargaEntity> findFirstByIdUsuarioAndEstadoAndIdTipoPagoAndMontoOrderByFechaCreacionDesc(UUID idUsuario,
            String estado, Long idTipoPago, BigDecimal monto);
}