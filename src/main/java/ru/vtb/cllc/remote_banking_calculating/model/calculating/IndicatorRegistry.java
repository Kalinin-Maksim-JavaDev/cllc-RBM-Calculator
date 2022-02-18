package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IndicatorRegistry {

    private final CodeGenerator codeGenerator;
    private Map<String, Class> map = new ConcurrentHashMap<>();

    public void update(List<IndicatorFormula> all) {
        Map<String, Class> mapNew = new ConcurrentHashMap<>();
        for (IndicatorFormula formula : all) {
            mapNew.put(formula.getName(), codeGenerator.createIndicatorClass(formula.getName(),
                    formula.getExpression(),
                    "long",
                    Record.class.getName()));
        }
        map = mapNew;
    }

    public <T> List<Indicator<T>> get(String[] names) {
        String[] sortedNames = names.clone();
        Arrays.sort(sortedNames);
        return map.entrySet().stream()
                .filter(kv -> Arrays.binarySearch(sortedNames, kv.getKey()) >= 0)
                .map(Map.Entry::getValue)
                .map(c -> {
                    try {
                        return (Indicator<T>) c.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }
}
