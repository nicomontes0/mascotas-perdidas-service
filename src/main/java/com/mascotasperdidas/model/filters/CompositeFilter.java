package com.mascotasperdidas.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class CompositeFilter implements Filter {

    FilterBuilder builder;
    ConnectorEnum connector;
    Filter actual;
    Filter next;

    public CompositeFilter(FilterBuilder builder) {
        this.builder = builder;
        connector = null;
        actual = null;
        next = null;
    }

    @Override
    public Filter build(String key, List<String> values) {
        int index = getFirstConnectorIndex(values);
        String connectorStr = values.get(index);
        List<String> actualValues = values.subList(0, index);
        String nextKey = values.get(index + 1);
        List<String> nextValues = values.subList(index + 2, values.size());

        ConnectorEnum actualConnector = ConnectorEnum.getFromValue(connectorStr.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Error de parseo al crear conector de filtros"));
        return new CompositeFilterBuilder()
                .connector(actualConnector)
                .actual(builder.buildFilter(key, actualValues))
                .next(builder.buildFilter(nextKey, nextValues))
                .build();

    }

    @Override
    public String getKey() {
        return null;
    }

    private int getFirstConnectorIndex(List<String> values) {
        int orIndex = values.indexOf("or");
        int andIndex = values.indexOf("and");

        if (orIndex < 0 && andIndex < 0) {
            return -1;
        }

        orIndex = ifNotContainsIndexEnsureHigherValueThanOtherIndex(orIndex, andIndex);
        andIndex = ifNotContainsIndexEnsureHigherValueThanOtherIndex(andIndex, orIndex);

        return lowerValue(orIndex, andIndex);
    }

    private int lowerValue(int anIndex, int otherIndex) {
        return anIndex < otherIndex ? anIndex : otherIndex;
    }

    private int ifNotContainsIndexEnsureHigherValueThanOtherIndex(int index, int otherIndex) {
        return index < 0 ? otherIndex + 1 : index;
    }

}
