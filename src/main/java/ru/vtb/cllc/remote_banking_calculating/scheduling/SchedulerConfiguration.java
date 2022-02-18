package ru.vtb.cllc.remote_banking_calculating.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@ConditionalOnProperty("cllc.formulas-loading.enabled")
@RequiredArgsConstructor
public class SchedulerConfiguration {

    private final FormulasLoader formulasLoader;

    @Scheduled(timeUnit = TimeUnit.MINUTES,
            fixedRateString = "${cllc.formulas-loading.scheduler.fixedRate}")
    public void loadFormulas() {
        formulasLoader.load();
    }
}
