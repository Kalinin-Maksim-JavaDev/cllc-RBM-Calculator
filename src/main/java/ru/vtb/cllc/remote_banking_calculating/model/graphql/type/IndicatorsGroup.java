package ru.vtb.cllc.remote_banking_calculating.model.graphql.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndicatorsGroup {

    private final String name;
    private final List<IndicatorsGroupPart> indicatorsGroupParts;

    private final List<IndicatorsGroup> subGroups;

    public IndicatorsGroup(String name, Map<Integer, Map<String, IndicatorValue>> indicatorsGroup, List<IndicatorsGroup> subGroups) {
        this.name = name;
        this.indicatorsGroupParts = new ArrayList<>();
        for (int part : indicatorsGroup.keySet()) {
            this.indicatorsGroupParts.add(new IndicatorsGroupPart(part,
                    indicatorsGroup.get(part).entrySet().stream()
                            .map(kv -> kv.getValue())
                            .collect(Collectors.toList())));
        }
        this.subGroups = List.copyOf(subGroups);
    }
}

