package ru.vtb.cllc.remote_banking_calculating.scheduling;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@ConditionalOnProperty("cllc.formulas-loading.enabled")
public class SchedulerConfiguration {

    FormulasLoader formulasLoader;

    @Scheduled(timeUnit = TimeUnit.MINUTES,
            fixedRateString = "${cllc.formulas-loading.scheduler.fixedRate}",
            cron = "${cllc.formulas-loading.scheduler.cron}")
    public void loadFormulas() {
        formulasLoader.load();
    }
}
