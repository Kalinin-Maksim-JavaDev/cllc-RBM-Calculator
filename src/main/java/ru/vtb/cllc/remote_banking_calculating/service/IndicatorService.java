package ru.vtb.cllc.remote_banking_calculating.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCase;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCaseRepository;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorValue;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class IndicatorService {

    private final ShowCaseRepository showCaseRepository;

    private final IndicatorRegistry indicatorRegistry;

    @Autowired
    public IndicatorService(ShowCaseRepository showCaseRepository, IndicatorRegistry indicatorRegistry) {
        this.showCaseRepository = showCaseRepository;
        this.indicatorRegistry = indicatorRegistry;
    }

    public <Group> Map<Group, Map<String, IndicatorValue>> calculate(LocalDate begin, LocalDate end, Integer id_user, Function<Record, Group> demension, List<String> indicatorNames) {

        List<ShowCase> showCases = showCaseRepository.get(begin, end);

        Stream<Record> recordStream = showCases.stream()
                .flatMap(showCase -> Arrays.stream(showCase.getRecords()));

        List<Indicator> indicators = indicatorRegistry.get(indicatorNames);

        Function<Record, Map<String, IndicatorValue>> indicateRecord = (Record record) ->
                indicators.stream()
                        .collect(toMap(Indicator::getName, indicator -> indicator.tryApply(record)));

        var sumAndIndicate = Collector.of(Record::new, Record::sum, Record::sum, indicateRecord::apply);

        Map<Group, Map<String, IndicatorValue>> indicatorsGroup = recordStream
                .filter(record -> Objects.isNull(id_user) || record.id_user == id_user)
                .collect(groupingBy(demension, sumAndIndicate));

        return indicatorsGroup;
    }
}