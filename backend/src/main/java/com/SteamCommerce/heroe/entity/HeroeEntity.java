package com.SteamCommerce.heroe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "heroe")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeroeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_heroe", updatable = false, nullable = false)
    private Long idHeroe;

    @Column(name = "nombre", length = 150, nullable = false, unique = true)
    private String nombre;

    @Column(name = "nombre_interno", length = 150)
    private String nombreInterno;
}
