package ru.vtb.cllc.remote_banking_calculating.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IndicatorFormulaRepository extends CrudRepository<IndicatorFormula, UUID> {

    @Query("select f from IndicatorFormula f where :date between f.period.begin and f.period.end " +
            "or (f.period.end is null and f.period.begin > :date)")
    List<IndicatorFormula> findByPeriodIn(LocalDate date);

}
