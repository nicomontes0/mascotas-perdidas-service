package com.mascotasperdidas.model.filters;

import java.util.Arrays;
import java.util.Optional;

public enum ConnectorEnum {
    OR,
    AND;

    public static Optional<ConnectorEnum> getFromValue(String value) {
        return Arrays.stream(ConnectorEnum.values())
                .filter(connector -> connector.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
