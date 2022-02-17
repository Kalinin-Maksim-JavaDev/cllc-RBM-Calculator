package ru.vtb.cllc.remote_banking_calculating.scheduling;

import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.dao.IndicatorFormulaRepository;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.FormulaRegistry;

import java.time.LocalDate;

@Component
public class FormulasLoader {

    IndicatorFormulaRepository repository;
    FormulaRegistry registry;

    public void load() {

        registry.update(repository.findByPeriodIn(LocalDate.now()));
    }
}
