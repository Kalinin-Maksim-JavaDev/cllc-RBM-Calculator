package ru.vtb.cllc.remote_banking_calculating.model.graphql.type;

import ru.vtb.cllc.remote_banking_calculating.model.graphql.GroupName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndicatorsGroup {

    private final GroupName name;
    private final List<IndicatorsGroupPart> indicatorsGroupParts;

    public IndicatorsGroup(GroupName name, Map<Integer, Map<String, IndicatorValue>> indicatorsGroup, List<IndicatorsGroup> subGroups) {
        this.name = name;
        this.indicatorsGroupParts = new ArrayList<>();
        for (int part : indicatorsGroup.keySet()) {
            this.indicatorsGroupParts.add(new IndicatorsGroupPart(description(part, name),
                    indicatorsGroup.get(part).entrySet().stream()
                            .map(kv -> kv.getValue())
                            .collect(Collectors.toList())));
        }
    }

    private String description(int part, GroupName name) {
        switch (name) {
            case BY_MONTH:
                return LocalDate.ofEpochDay(part).format(DateTimeFormatter.ofPattern("LLLL (YYYY)"));
            case BY_DAY:
                return LocalDate.ofEpochDay(part).format(DateTimeFormatter.ISO_LOCAL_DATE);
            default:
                return String.valueOf(part);
        }
    }
}

