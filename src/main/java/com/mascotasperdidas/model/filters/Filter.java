package com.mascotasperdidas.model.filters;

import java.util.List;

public interface Filter {

    Filter build(String key, List<String> values);
    String getKey();
}
