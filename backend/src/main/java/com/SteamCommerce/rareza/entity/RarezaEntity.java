package com.SteamCommerce.rareza.entity;

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
@Table(name = "rareza")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RarezaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rareza", updatable = false, nullable = false)
    private Long idRareza;

    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "nombre_interno", length = 100)
    private String nombreInterno;
}
