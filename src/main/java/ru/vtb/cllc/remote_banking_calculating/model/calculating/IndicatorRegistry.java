package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IndicatorRegistry {

    private Map<String, Indicator> map = new HashMap<>();

    public void update(List<IndicatorFormula> formulas) {
        Map<String, Indicator> mapNew = new ConcurrentHashMap<>();
        var codeGenerator = new CodeGenerator();
        for (IndicatorFormula formula : formulas) {
            codeGenerator.add(formula.getName(),
                    formula.getExpression(),
                    int.class.getName(),
                    Record.class.getName());
        }
        var loader = codeGenerator.compile();
        for (IndicatorFormula formula : formulas) {
            try {
                var clazz = loader.loadClass(formula.getName());
                Indicator indicator = (Indicator) clazz.getDeclaredConstructor().newInstance();
                ((IndicatorFormula) indicator).setName(formula.getName());
                ((IndicatorFormula) indicator).setExpression(formula.getExpression());
                ((IndicatorFormula) indicator).setPeriod(formula.getPeriod());
                mapNew.put(formula.getName(), indicator);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        map = mapNew;
    }

    public List<Indicator> get(List<String> names) {
        String[] sortedNames = names.toArray((new String[0]));
        Arrays.sort(sortedNames);
        return map.entrySet().stream()
                .filter(kv -> Arrays.binarySearch(sortedNames, kv.getKey()) >= 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<IndicatorFormula> getAll() {
        return map.values().stream()
                .map(e -> (IndicatorFormula) e)
                .collect(Collectors.toList());
    }
}
