package ru.vtb.cllc.remote_banking_calculating.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.dao.IndicatorFormulaRepository;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FormulasLoader {

    private final IndicatorFormulaRepository repository;
    private final IndicatorRegistry registry;

    public void load() {

        registry.update(repository.findByPeriodIn(LocalDate.now()));
    }
}
