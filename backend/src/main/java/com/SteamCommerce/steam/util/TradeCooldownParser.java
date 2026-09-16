package com.SteamCommerce.steam.util;

import com.SteamCommerce.steam.dto.SteamDescriptionItem;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TradeCooldownParser {

    // Formateador Estándar: "Sep 17, 2026 (13:00:00)" (Insensible a
    // mayúsculas/minúsculas)
    private static final DateTimeFormatter FORMATEADOR_ESTANDAR = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d, yyyy (HH:mm:ss)")
            .toFormatter(Locale.ENGLISH);

    // Formateador Legacy: "Thu Sep 17 13:00:00 2026"
    private static final DateTimeFormatter FORMATEADOR_LEGACY = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("EEE MMM d HH:mm:ss yyyy")
            .toFormatter(Locale.ENGLISH);

    // Captura formatos como: "Sep 17, 2026 (13:00:00)"
    private static final Pattern PATRON_ESTANDAR = Pattern.compile(
            "(?i)([a-z]{3}\\s+\\d{1,2},\\s+\\d{4}\\s+\\(\\d{2}:\\d{2}:\\d{2}\\))");

    // Captura formatos como: "Thu Sep 17 13:00:00 2026"
    private static final Pattern PATRON_LEGACY = Pattern.compile(
            "(?i)([a-z]{3}\\s+[a-z]{3}\\s+\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2}\\s+\\d{4})");

    /**
     * Extrae y parsea la fecha de restricción de intercambio desde la lista de
     * descripciones.
     */
    public static LocalDateTime extractCooldownDate(List<SteamDescriptionItem> descripciones) {
        if (descripciones == null || descripciones.isEmpty()) {
            return null;
        }

        for (SteamDescriptionItem item : descripciones) {
            String textoOriginal = item.getValue();
            if (textoOriginal == null || textoOriginal.isBlank()) {
                continue;
            }

            // Normalización: remueve \n, espacios invisibles (\u00A0) y etiquetas HTML
            String textoLimpio = limpiarTexto(textoOriginal);

            if (contienePalabraClaveCooldown(textoLimpio)) {
                log.info("Detectado texto de restricción: '{}'", textoLimpio);

                LocalDateTime fechaParseada = parsearFecha(textoLimpio);
                if (fechaParseada != null) {
                    log.info("Fecha de cooldown parseada correctamente: {}", fechaParseada);
                    return fechaParseada;
                }
            }
        }

        return null;
    }

    /**
     * Reemplaza caracteres invisibles, saltos de línea y normaliza espacios.
     */
    private static String limpiarTexto(String texto) {
        return texto.replaceAll("<[^>]*>", " ") // Remueve etiquetas HTML
                .replace("&nbsp;", " ") // Remueve entidades &nbsp;
                .replace('\u00A0', ' ') // Remueve espacio no-break (\u00A0)
                .replaceAll("\\s+", " ") // Convierte \n, \r y múltiples espacios en un solo espacio
                .trim();
    }

    /**
     * Verifica si el texto contiene la frase indicadora de restricción.
     */
    private static boolean contienePalabraClaveCooldown(String texto) {
        String textoMinusculas = texto.toLowerCase();
        return textoMinusculas.contains("trade cooldown until:") ||
                textoMinusculas.contains("on trade cooldown until:");
    }

    /**
     * Extrae el bloque de fecha con Expresiones Regulares y lo convierte a
     * LocalDateTime.
     */
    private static LocalDateTime parsearFecha(String texto) {
        // Intento 1: Formato Estándar ("Sep 17, 2026 (13:00:00)")
        Matcher matcherEstandar = PATRON_ESTANDAR.matcher(texto);
        if (matcherEstandar.find()) {
            String grupoFecha = matcherEstandar.group(1);
            try {
                return LocalDateTime.parse(grupoFecha, FORMATEADOR_ESTANDAR);
            } catch (DateTimeParseException e) {
                log.warn("Falló el parseo estándar para la cadena: '{}'", grupoFecha);
            }
        }

        // Intento 2: Formato Legacy ("Thu Sep 17 13:00:00 2026")
        Matcher matcherLegacy = PATRON_LEGACY.matcher(texto);
        if (matcherLegacy.find()) {
            String grupoFecha = matcherLegacy.group(1);
            try {
                return LocalDateTime.parse(grupoFecha, FORMATEADOR_LEGACY);
            } catch (DateTimeParseException e) {
                log.warn("Falló el parseo legacy para la cadena: '{}'", grupoFecha);
            }
        }

        log.error("Se encontró el texto de Cooldown pero la fecha no coincidió con ningún patrón: '{}'", texto);
        return null;
    }
}