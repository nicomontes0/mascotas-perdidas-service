package com.mascotasperdidas.model.filters;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FilterBuilder {

    private static final String REGEX = "([\\\\*+()!:^\\[\\]\"}~?|&/])";
    private static final String REPLACEMENT = "\\\\$1";

    private final Map<String, Filter> filterFactory = Map.of(
        "range", new RangeFilter(),
        "equals", new EqualsFilter(),
        "notIn", new InListFilter(),
        "in", new InListFilter(),
        "composite", new CompositeFilter(this),
        "match", new MatchPhraseFilter(),
        "startsWith", new StartsWithFilter(),
        "contains", new ContainsFilter(),
        "notExists", new ExistsFilter()
    );

    public List<Filter> build(Map<String, String> mappedFilters) {
        try {
            return doBuild(mappedFilters);
        } catch (IndexOutOfBoundsException ex) {
            log.error("Error de parseo de filtros", ex);
            throw new IllegalArgumentException();
        }
    }

    private List<Filter> doBuild(Map<String, String> mappedFilters) {
        List<Filter> filters = new ArrayList<>();
        mappedFilters.forEach((key, value) -> {
            List<String> valueSplit = Arrays.asList(value.split(","));
            Filter filter = buildFilter(key, valueSplit);
            filters.add(filter);
        });

        return filters;
    }

    public Filter buildFilter(String key, List<String> valueSplit) {
        String filterType;
        List<String> values;

        if (isComposite(valueSplit)) {
            filterType = "composite";
            values = valueSplit;
        } else {
            filterType = valueSplit.getFirst();
            values = valueSplit.subList(1, valueSplit.size())
                .stream()
                .map(FilterBuilder::escapeSpecialCharacters)
                .collect(Collectors.toList());
        }
        validate(filterType);
        return filterFactory.get(filterType).build(key, values);
    }

    private void validate(String filterType) {
        if (!filterFactory.containsKey(filterType)) {
            throw new IllegalArgumentException("Tipo de filtro invalido: " + filterType);
        }
    }

    private boolean isComposite(List<String> values) {
        return values.contains("or") || values.contains("and");
    }

    private static String escapeSpecialCharacters(String value) {
        return value.replaceAll(REGEX, REPLACEMENT);
    }
}