package com.mascotasperdidas.utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
public final class FiltersUtils {

    /**
     * Soporta propiedades anidadas como "user.name" o "owner.address.city".
     */
    public static Path<?> getPath(Root<?> root, String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key del filtro vacía");
        }
        if (!key.contains(".")) {
            return root.get(key);
        }
        String[] parts = key.split("\\.");
        Path<?> p = root;
        for (String part : parts) {
            p = p.get(part);
        }
        return p;
    }

    /**
     * Convierte String -> targetType. Usa ConversionService si está disponible.
     * Maneja LocalDate/LocalDateTime/LocalTime en ISO.
     */
    public static Object convert(String raw, Class<?> targetType, ConversionService cs) {
        if (raw == null) return null;
        if (targetType == null) return raw;

        if (targetType.equals(OffsetDateTime.class))
            return LocalDate.parse(raw)
                    .atStartOfDay()
                    .atOffset(ZoneOffset.UTC);

        if (cs != null && cs.canConvert(String.class, targetType)) {
            Object converted = cs.convert(raw, targetType);
            if (converted != null) return converted;
        }

        // Fallbacks básicos
        if (targetType.equals(String.class)) return raw;
        if (targetType.equals(Integer.class) || targetType.equals(int.class)) return Integer.valueOf(raw);
        if (targetType.equals(Long.class) || targetType.equals(long.class)) return Long.valueOf(raw);
        if (targetType.equals(Double.class) || targetType.equals(double.class)) return Double.valueOf(raw);
        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) return Boolean.valueOf(raw);

        return raw;
    }

    public static OffsetDateTime convertToDateTime(String fechaStr) {
        LocalDate localDate = LocalDate.parse(fechaStr);
        return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

}
