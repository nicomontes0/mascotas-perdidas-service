package com.mascotasperdidas.model.filters;

import com.mascotasperdidas.utils.FiltersUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@AllArgsConstructor
public class InListFilter implements Filter {

    String key;
    List<String> values;

    public InListFilter() {
        key = null;
        values = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        List<String> valuesWithoutEmpty = values.stream()
                .filter(value -> !StringUtils.isEmpty(value))
                .collect(Collectors.toList());

        return new InListFilterBuilder()
                .key(key)
                .values(valuesWithoutEmpty)
                .build();
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb, ConversionService conversionService) {
        Path<?> path = FiltersUtils.getPath(root, key);
        CriteriaBuilder.In<Object> in = cb.in(path);
        Class<?> javaType = path.getJavaType();

        if (values == null || values.isEmpty()) {
            throw new RuntimeException("Filtro vacio");
        }

        for (String raw : values) {
            Object converted = FiltersUtils.convert(raw, javaType, conversionService);
            in.value(converted);
        }
        return in;
    }
}