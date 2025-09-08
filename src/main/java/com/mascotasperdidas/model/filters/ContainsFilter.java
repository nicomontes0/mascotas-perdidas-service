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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.convert.ConversionService;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainsFilter implements Filter{

    String key;
    String value;

    @Override
    public Filter build(String key, List<String> values) {
        return new ContainsFilterBuilder()
                .key(key)
                .value("*" + values.get(0) + "*")
                .build();
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb, ConversionService conversionService) {
        Path<?> path = FiltersUtils.getPath(root, key);
        if (!String.class.equals(path.getJavaType())) {
            throw new RuntimeException("El parametro no es string");
        }
        String pattern = value == null ? "%%" : "%" + value.toLowerCase() + "%";
        return cb.like(cb.lower((Expression<String>) path), pattern);
    }
}
