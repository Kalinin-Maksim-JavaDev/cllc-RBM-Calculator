package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.graphql.type.IndicatorValue;

public interface Indicator {

    int apply(Record record);

    default IndicatorValue tryApply(Record record) {

        try {
            int value = apply(record);
            return new IndicatorValue(getName(), value, "");
        } catch (Exception e) {
            return new IndicatorValue(getName(), Integer.MIN_VALUE, e.getMessage());
        }
    }

    default String getName() {
        return this.getClass().getName();
    }
}
