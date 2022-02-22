package ru.vtb.cllc.remote_banking_calculating.GraphQL;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.dao.IndicatorFormulaRepository;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
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
    private final IndicatorFormulaRepository repository;

    public IndicatorsGroup group(String name, List<String> indicatorNames, String begin, String end, Integer id_user) {

        Function<Record, Integer> demension = null;

        switch (name) {
            case "byMonth":
                demension = record -> (Integer) record.getMonth();
                break;
            case "byDay":
                demension = record -> (Integer) record.getEpochDay();
                break;
        }
        Map<Integer, Map<String, IndicatorValue>> indicatorsGroup = service.calculate(LocalDate.parse(begin, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(end, DateTimeFormatter.ISO_DATE),
                id_user,
                demension,
                indicatorNames);

        return new IndicatorsGroup(name, indicatorsGroup, List.of());
    }

    public List<IndicatorFormula> formulas() {
        return repository.findByPeriodIn(LocalDate.now());
    }
}