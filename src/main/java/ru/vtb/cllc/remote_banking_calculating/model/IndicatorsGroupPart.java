package ru.vtb.cllc.remote_banking_calculating.model;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class IndicatorsGroupPart {

    private final int part;
    private final List<IndicatorValue> indicators;
}
