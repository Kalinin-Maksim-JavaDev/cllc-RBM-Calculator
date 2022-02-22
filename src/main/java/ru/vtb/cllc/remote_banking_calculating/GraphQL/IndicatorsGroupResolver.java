package ru.vtb.cllc.remote_banking_calculating.GraphQL;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.Grouping;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorValue;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorsGroup;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IndicatorsGroupResolver implements GraphQLQueryResolver {

    private final IndicatorService service;
    private final IndicatorRegistry registry;

    public List<IndicatorsGroup> groups(Grouping grouping, List<String> indicatorNames, String begin, String end, Integer id_user) {

        Map<Integer, Map<String, IndicatorValue>> indicatorsGroup = service.calculate(LocalDate.parse(begin, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(end, DateTimeFormatter.ISO_DATE),
                id_user,
                grouping.getClassifier(),
                indicatorNames);
        List<IndicatorsGroup> indicatorsGroups = new ArrayList<>();
        for (int part : indicatorsGroup.keySet()) {
            indicatorsGroups.add(new IndicatorsGroup(grouping.description(part),
                    indicatorsGroup.get(part).entrySet().stream()
                            .map(kv -> kv.getValue())
                            .collect(Collectors.toList())));
        }
        return indicatorsGroups;
    }

    public List<IndicatorFormula> formulas() {
        return registry.getAll();
    }
}