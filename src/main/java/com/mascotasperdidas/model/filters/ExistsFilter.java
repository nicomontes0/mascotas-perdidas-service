package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class ExistsFilter implements Filter {
    String key;

    public ExistsFilter() {
        key = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        return new ExistsFilterBuilder()
                .key(key)
                .build();
    }
}
