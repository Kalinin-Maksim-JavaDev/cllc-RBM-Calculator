package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class IndicatorRegistry {

    private final CodeGenerator codeGenerator;
    private final Map<String, Class> map = new ConcurrentHashMap<>();

    public void update(List<IndicatorFormula> all) {
        map.clear();
        for (IndicatorFormula formula : all) {
            map.put(formula.getName(), codeGenerator.createIndicatorClass(formula.getName(),
                    formula.getExpression(),
                    "long",
                    Record.class.getName()));
        }
    }

    public <T> List<Indicator<T>> get(String name) {
        var clazz = codeGenerator.createIndicatorClass("AHT", " t_ring , t_inb , t_hold ,  t_acw, n_inb -> (t_ring + t_inb + t_hold + t_acw) / n_inb", "long", Record.class.getName());
        try {
            return List.of((Indicator) clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
