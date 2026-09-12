package com.SteamCommerce.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
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

    @Column(name = "app_id")
    private String appId;

    @Column(name = "context_id")
    private String contextId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "name", length = 500)
    private String name;

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

    @Column(name = "commodity")
    private Boolean commodity;

    @Column(name = "market_tradable_restriction")
    private Integer marketTradableRestriction;

    @Column(name = "trade_cooldown_until")
    private LocalDateTime tradeCooldownUntil;

    @Column(name = "id_tipo_item", nullable = false)
    private Long idTipoItem;

    @Column(name = "id_rareza")
    private Long idRareza;

    @Column(name = "id_heroe")
    private Long idHeroe;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    public void generarPublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }
}
