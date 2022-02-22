package ru.vtb.cllc.remote_banking_calculating.GraphQL;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.GroupName;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorValue;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorsGroup;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IndicatorsGroupResolver implements GraphQLQueryResolver {

    private final IndicatorService service;
    private final IndicatorRegistry registry;

    public IndicatorsGroup group(GroupName name, List<String> indicatorNames, String begin, String end, Integer id_user) {

        Function<Record, Integer> classifier = null;

        switch (name) {
            case BY_MONTH:
                classifier = Record::getEpochMonthFirstDay;
                break;
            case BY_DAY:
                classifier = Record::getEpochDay;
                break;
            case BY_DIVISION:
                classifier = Record::getId_tree_division;
                break;
            case BY_REGION:
                classifier = Record::getId_region;
                break;
            case BY_USER:
                classifier = Record::getId_user;
                break;
            case BY_LINE:
                classifier = Record::getId_line;
                break;
        }
        Map<Integer, Map<String, IndicatorValue>> indicatorsGroup = service.calculate(LocalDate.parse(begin, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(end, DateTimeFormatter.ISO_DATE),
                id_user,
                classifier,
                indicatorNames);

        return new IndicatorsGroup(name, indicatorsGroup, List.of());
    }

    public List<IndicatorFormula> formulas() {
        return registry.getAll();
    }
}