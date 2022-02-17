package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FormulaRegistry {

    CodeGenerator codeGenerator;
    private Map<String, Class> map = new ConcurrentHashMap<>();

    public void update(List<IndicatorFormula> all) {
        map.clear();
        for (IndicatorFormula formula : all) {
            map.put(formula.getName(), codeGenerator.createIndicatorClass(formula.getName(),
                    formula.getExpression(),
                    "long",
                    Record.class.getName()));
        }
    }
}
