package com.SteamCommerce.recarga.entity;

import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "recarga")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecargaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recarga", updatable = false, nullable = false)
    private Long idRecarga;

    @Column(name = "id_usuario", nullable = false)
    private UUID idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private UsuarioEntity usuario;

    @Column(name = "id_tipo_pago", nullable = false)
    private Long idTipoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_pago", insertable = false, updatable = false)
    private TipoPagoEntity tipoPago;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "moneda", length = 10, nullable = false)
    @Builder.Default
    private String moneda = "PEN";

    @Column(name = "codigo_recarga", length = 20, nullable = false, unique = true)
    private String codigoRecarga;

    @Column(name = "mensaje_pago", length = 50)
    private String mensajePago;

    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";

    @Column(name = "comprobante_url", length = 255)
    private String comprobanteUrl;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;
}
