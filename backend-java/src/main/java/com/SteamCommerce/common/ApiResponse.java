package com.SteamCommerce.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "message", "error", "status", "data" })
public class ApiResponse<T> {
    private String message;
    private String error;
    private int status;
    private T data;

    // Método para ÉXITO
    public static <T> ApiResponse<T> success(String message, int status) {
        return new ApiResponse<T>(message, null, status, null);
    }

    // Método para ERROR
    public static <T> ApiResponse<T> error(String error, int status) {
        return new ApiResponse<T>(null, error, status, null);
    }

    public static <T> ApiResponse<T> success(String message, int status, T data) {
        return new ApiResponse<T>(message, null, status, data);
    }
}