package ru.vtb.cllc.remote_banking_calculating.model.graphql.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndicatorValue {

    private final String name;
    private final int value;
    private final String error;
}
