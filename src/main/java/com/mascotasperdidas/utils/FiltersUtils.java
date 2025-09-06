package com.mascotasperdidas.utils;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FiltersUtils {
    public static final String NOT = "not";

    public static Map<String, String> removeNotFilters(Map<String, String> mappedFilters) {
        Map<String, String> result = new HashMap<>();

        mappedFilters.keySet().stream()
                .filter(key -> !mappedFilters.get(key).startsWith(NOT))
                .forEach(key -> result.put(key, mappedFilters.get(key)));

        return result;
    }

    public static Map<String, String> removeFilters(Map<String, String> mappedFilters) {
        Map<String, String> result = new HashMap<>();

        mappedFilters.keySet().stream()
                .filter(key -> mappedFilters.get(key).startsWith(NOT))
                .forEach(key -> result.put(key, mappedFilters.get(key)));

        return result;
    }
}
