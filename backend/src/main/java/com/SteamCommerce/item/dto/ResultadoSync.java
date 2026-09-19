package com.SteamCommerce.item.dto;

public record ResultadoSync(int guardados, int actualizados, int total) {

    public static ResultadoSync vacio() {
        return new ResultadoSync(0, 0, 0);
    }
}