package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import ru.vtb.cllc.remote_banking_calculating.model.Record;

public abstract class GenericIndicator {

    public abstract void add(Record record);

    public abstract void add(GenericIndicator other);

    public abstract long value();

    public static GenericIndicator sum(GenericIndicator g1, GenericIndicator g2) {
        g1.add(g2);
        return g1;
    }
}
