package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
