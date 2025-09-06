package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
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
}