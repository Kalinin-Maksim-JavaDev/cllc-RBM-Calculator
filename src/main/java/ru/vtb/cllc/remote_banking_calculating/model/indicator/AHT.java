package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import ru.vtb.cllc.remote_banking_calculating.model.Record;

public class AHT implements Indicator {

    public Long apply(Record record) {
        var t_ring = record.t_ring;
        var t_inb = record.t_inb;
        var t_hold = record.t_hold;
        var t_acw = record.t_acw;
        var n_inb = record.n_inb;
        try {
            return (t_ring + t_inb + t_hold + t_acw) / n_inb;
        } catch (ArithmeticException ex) {
            return 0L;
        }
    }
}
