package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

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
}