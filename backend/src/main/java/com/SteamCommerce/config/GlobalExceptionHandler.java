package com.SteamCommerce.config;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.config.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // Método helper para ERROR con HTTP status REAL
        private ResponseEntity<ApiResponse<Void>> buildErrorResponse(String error, int statusCode) {
                ApiResponse<Void> response = ApiResponse.error(error, statusCode);
                return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(response); // ← HTTP REAL
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
                return buildErrorResponse(ex.getMessage(), 404); // ← HTTP 404
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
                return buildErrorResponse(ex.getMessage(), 400); // ← HTTP 400
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
                return buildErrorResponse(ex.getMessage(), 400); // ← HTTP 400
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
                return buildErrorResponse("El cuerpo de la petición (JSON) no es válido o está mal formateado", 400);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
                return buildErrorResponse("Falta el parámetro requerido: " + ex.getParameterName(), 400);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
                String mensaje = String.format("El parámetro '%s' con valor '%s' no tiene un formato válido",
                                ex.getName(), ex.getValue());
                return buildErrorResponse(mensaje, 400);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Datos de petición inválidos");
                return buildErrorResponse(errorMessage, 400);
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
                return buildErrorResponse("Método HTTP no soportado: " + ex.getMethod(), 405);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
                return buildErrorResponse("Ruta no encontrada: " + ex.getResourcePath(), 404);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
                boolean esNoEncontrado = ex.getMessage() != null
                                && ex.getMessage().toLowerCase().contains("no encontrado");
                int statusCode = esNoEncontrado ? 404 : 500;
                String error = ex.getMessage() != null ? ex.getMessage() : "Error en el servidor";
                return buildErrorResponse(error, statusCode);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
                return buildErrorResponse("Error interno del servidor: " + ex.getMessage(), 500);
        }
}