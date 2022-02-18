package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import ru.vtb.cllc.remote_banking_calculating.model.Record;

public interface Indicator<T> {

    T apply(Record record);
}
