package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Registry {

    private Map<String, String> map = new ConcurrentHashMap<>();

    public void update(List<IndicatorFormula> all) {
        for (IndicatorFormula formula : all) {
            map.put(formula.getName(), formula.getExpression());
        }
    }
}
