package ru.vtb.cllc.remote_banking_calculating.GraphQL;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Group;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroupResolver implements GraphQLQueryResolver {

    private final IndicatorService service;

    public Group group(String name, List<String> indicatorNames, int id_user) {

        Function<Record, Integer> demension = null;

        switch (name) {
            case "byMonth":
                demension = Record::getMonth;
                break;
        }
        Map<Integer, Map<String, Object>> indicatorsGroup = service.calculate(id_user,
                demension,
                indicatorNames);

        return new Group(name, indicatorNames, List.of());
    }
}