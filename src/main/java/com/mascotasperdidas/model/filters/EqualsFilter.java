package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

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
                .value(values.get(0))
                .build();
    }
}