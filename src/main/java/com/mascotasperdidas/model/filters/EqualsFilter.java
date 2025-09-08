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

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class EqualsFilter implements Filter {

    String key;
    String value;

    public EqualsFilter() {
        key = null;
        value = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        return new EqualsFilterBuilder()
                .key(key)
                .value(values.getFirst())
                .build();
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb, ConversionService conversionService) {
        Path<?> path = FiltersUtils.getPath(root, key);
        Object v = FiltersUtils.convert(value, path.getJavaType(), conversionService);
        return cb.equal(path, v);
    }
}