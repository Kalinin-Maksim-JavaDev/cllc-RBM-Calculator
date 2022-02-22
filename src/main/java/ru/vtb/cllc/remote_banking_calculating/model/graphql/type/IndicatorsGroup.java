package ru.vtb.cllc.remote_banking_calculating.model.graphql.type;

import ru.vtb.cllc.remote_banking_calculating.model.graphql.Grouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndicatorsGroup {

    private final Grouping grouping;
    private final List<IndicatorsGroupPart> indicatorsGroupParts;

    public IndicatorsGroup(Grouping grouping, Map<Integer, Map<String, IndicatorValue>> indicatorsGroup, List<IndicatorsGroup> subGroups) {
        this.grouping = grouping;
        this.indicatorsGroupParts = new ArrayList<>();
        for (int part : indicatorsGroup.keySet()) {
            this.indicatorsGroupParts.add(new IndicatorsGroupPart(grouping.description(part),
                    indicatorsGroup.get(part).entrySet().stream()
                            .map(kv -> kv.getValue())
                            .collect(Collectors.toList())));
        }
    }


}

