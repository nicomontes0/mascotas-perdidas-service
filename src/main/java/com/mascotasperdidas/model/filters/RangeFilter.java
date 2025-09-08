package com.mascotasperdidas.model.filters;

import com.mascotasperdidas.utils.FiltersUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static com.mascotasperdidas.utils.FiltersUtils.convertToDateTime;

@Value
@Builder
@AllArgsConstructor
@Slf4j
public class RangeFilter implements Filter {

    String key;
    String from;
    String to;

    public RangeFilter() {
        key = null;
        from = null;
        to = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        return new RangeFilterBuilder()
                .key(key)
                .from(getValue(values.get(0)))
                .to(values.size() > 1 ? getValue(values.get(1)) : null)
                .build();
    }

    private String getValue(String value) {
        return StringUtils.isEmpty(value) ? null : value;
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb, ConversionService conversionService) {
        Path<?> path = FiltersUtils.getPath(root, key);
        Class<?> javaType = path.getJavaType();

        Object oFrom = FiltersUtils.convert(from, javaType, conversionService);
        Object oTo = FiltersUtils.convert(to, javaType, conversionService);

        if (oFrom == null && oTo == null) return null;

        if (Comparable.class.isAssignableFrom(javaType)) {
            Comparable cFrom = (Comparable) oFrom;
            Comparable cTo = (Comparable) oTo;

            if (cFrom != null && cTo != null) {
                return cb.between((Expression<? extends Comparable>) path, cFrom, cTo);
            } else if (cFrom != null) {
                return cb.greaterThanOrEqualTo((Expression<? extends Comparable>) path, cFrom);
            } else {
                return cb.lessThanOrEqualTo((Expression<? extends Comparable>) path, cTo);
            }
        }

        throw new RuntimeException("Valores no comparables.");
    }
}