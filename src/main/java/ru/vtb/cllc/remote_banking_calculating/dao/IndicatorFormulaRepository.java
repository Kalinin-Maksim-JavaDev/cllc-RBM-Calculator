package ru.vtb.cllc.remote_banking_calculating.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;

import java.util.List;
import java.util.UUID;

@Repository
public interface IndicatorFormulaRepository extends CrudRepository<IndicatorFormula, UUID> {

    @Override
    List<IndicatorFormula> findAll();
}
