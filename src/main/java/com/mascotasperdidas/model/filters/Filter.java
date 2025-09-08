package com.mascotasperdidas.model.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.core.convert.ConversionService;

import java.util.List;

public interface Filter {

    Filter build(String key, List<String> values);
    String getKey();
    Predicate toPredicate(Root<?> root,
                          CriteriaQuery<?> query,
                          CriteriaBuilder cb,
                          ConversionService conversionService);
}
