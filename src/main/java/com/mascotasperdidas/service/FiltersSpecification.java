package com.mascotasperdidas.service;

import com.mascotasperdidas.model.filters.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FiltersSpecification<T> implements Specification<T> {

    private final List<Filter> filters;
    private final ConversionService conversionService;

    public FiltersSpecification(List<Filter> filters, ConversionService conversionService) {
        this.filters = filters != null ? filters : Collections.emptyList();
        this.conversionService = conversionService;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // para cada filter pedimos su predicate (puede devolver null si decide ignorar)
        Predicate[] preds = filters.stream()
                .map(f -> {
                    try {
                        return f.toPredicate(root, query, cb, conversionService);
                    } catch (Exception e) {
                        log.error("Error construyendo predicate para filtro {}: {}", f.getKey(), e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);

        if (preds.length == 0) {
            return cb.conjunction();
        }
        return cb.and(preds);
    }
}