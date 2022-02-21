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

    @Query("select f\n" +
            "from IndicatorFormula f\n" +
            "where :date between f.period.begin and f.period.end\n" +
            "   or (f.period.end is null and :date >= f.period.begin)")
    List<IndicatorFormula> findByPeriodIn(LocalDate date);

}
