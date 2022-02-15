package ru.vtb.cllc.remote_banking_calculating.event;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.dao.IndicatorFormulaRepository;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.Registry;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;

import java.util.List;

@Component
public class OnStart implements ApplicationRunner {

    private final IndicatorFormulaRepository indicatorFormulaRepository;
    private final Registry registry;

    public OnStart(IndicatorFormulaRepository indicatorFormulaRepository, Registry registry) {
        this.indicatorFormulaRepository = indicatorFormulaRepository;
        this.registry = registry;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<IndicatorFormula> all = indicatorFormulaRepository.findAll();
        registry.update(all);
    }
}
