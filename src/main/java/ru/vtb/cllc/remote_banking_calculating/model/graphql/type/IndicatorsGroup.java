package ru.vtb.cllc.remote_banking_calculating.model.graphql.type;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class IndicatorsGroup {

    private final String group;
    private final List<IndicatorValue> indicators;
}
