package com.mascotasperdidas.utils;

public class JwtUtils {

    /**
     * Extrae el JWT del header Authorization
     * @param authorizationHeader el valor del header Authorization
     * @return JWT puro (string)
     * @throws IllegalArgumentException si el header es nulo o mal formado
     */
    public static String extractJwtFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization header está vacío");
        }

        // Verifica que empiece con "Bearer "
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header mal formado. Debe empezar con 'Bearer '");
        }

        // Retorna la parte después de "Bearer "
        return authorizationHeader.substring(7).trim();
    }
}
