package com.SteamCommerce.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import com.SteamCommerce.heroe.entity.HeroeEntity;
import com.SteamCommerce.rareza.entity.RarezaEntity;
import com.SteamCommerce.tipo_item.entity.TipoItemEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item", updatable = false, nullable = false)
    private Long idItem;

    @Column(name = "public_id", length = 50, nullable = false, unique = true)
    @Builder.Default
    private String publicId = UUID.randomUUID().toString();

    @Column(name = "asset_id")
    private String assetId;

    @Column(name = "market_name", length = 500)
    private String marketName;

    @Column(name = "market_hash_name", length = 500)
    private String marketHashName;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "color")
    private String color;

    @Column(name = "tradable")
    private Boolean tradable;

    @Column(name = "marketable")
    private Boolean marketable;

    @Column(name = "market_tradable_restriction")
    private Integer marketTradableRestriction;

    @Column(name = "trade_cooldown_until")
    private LocalDateTime tradeCooldownUntil;

    @Column(name = "id_tipo_item")
    private Long idTipoItem;

    @Column(name = "id_rareza")
    private Long idRareza;

    @Column(name = "id_heroe")
    private Long idHeroe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_item", insertable = false, updatable = false)
    private TipoItemEntity tipoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rareza", insertable = false, updatable = false)
    private RarezaEntity rareza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_heroe", insertable = false, updatable = false)
    private HeroeEntity heroe;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void generarPublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }
}
