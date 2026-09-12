package com.SteamCommerce.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private UUID idUsuario;

    @Column(name = "steam_id", unique = true, nullable = false, length = 100)
    private String steamId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "trade_url")
    private String tradeUrl;

    @Column(name = "saldo_soles", precision = 10, scale = 2)
    private BigDecimal saldoSoles;

    @Column(name = "role", length = 50)
    private String role;
}
