package ru.vtb.cllc.remote_banking_calculating.GraphQL;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Group;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupResolver implements GraphQLQueryResolver {

    private final IndicatorService service;

    public <G> Group group(String name, List<String> indicatorNames, String begin, String end, Integer id_user) {

        Function<Record, G> demension = null;

        switch (name) {
            case "byMonth":
                demension = record -> (G) record.getMonth();
                break;
            case "byDay":
                demension = record -> (G) record.getEpochDay();
                break;
        }
        var indicatorsGroup = service.calculate(LocalDate.parse(begin, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(end, DateTimeFormatter.ISO_DATE),
                id_user,
                demension,
                indicatorNames);

        return new Group(name, indicatorNames, List.of());
    }
}