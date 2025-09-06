package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class MatchPhraseFilter implements Filter {

    String key;
    String value;

    public MatchPhraseFilter() {
        value = null;
        key = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        return new MatchPhraseFilterBuilder()
                .key(key)
                .value(values.get(0))
                .build();
    }
}
