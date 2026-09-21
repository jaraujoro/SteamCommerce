package com.SteamCommerce.instruccion_pago.entity;

import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instruccion_pago")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstruccionPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instruccion", updatable = false, nullable = false)
    private Long idInstruccion;

    @Column(name = "id_tipo_pago", nullable = false)
    private Long idTipoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_pago", insertable = false, updatable = false)
    private TipoPagoEntity tipoPago;

    @Column(name = "titular", length = 100)
    private String titular;

    @Column(name = "numero_pago", length = 50)
    private String numeroPago;

    @Column(name = "cuenta_bancaria", length = 50)
    private String cuentaBancaria;

    @Column(name = "cci", length = 50)
    private String cci;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo;

    @Column(name = "monto_maximo", precision = 10, scale = 2)
    private BigDecimal montoMaximo;

    @Column(name = "instrucciones_texto", columnDefinition = "TEXT")
    private String instruccionesTexto;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
