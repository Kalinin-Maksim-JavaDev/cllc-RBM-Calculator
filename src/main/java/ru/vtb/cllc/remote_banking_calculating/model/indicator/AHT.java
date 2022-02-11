package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import lombok.AllArgsConstructor;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AHT {

    long recordsCount;
    Set<String> currentThreadNames = new HashSet<>();
    long t_ring,t_inb,t_hold,t_acw,n_inb;

    @Override
    public String toString() {
        return "AHT{" +
                "recordsCount=" + recordsCount +
                ", aht=" + aht() +
                ", currentThreadNames=" + currentThreadNames +
                '}';
    }

    public void add(Record record){
        recordsCount++;
        currentThreadNames.add(Thread.currentThread().getName());
        t_ring+=record.t_ring;
        t_inb+=record.t_inb;
        t_hold+=record.t_hold;
        t_acw+=record.t_acw;
        n_inb+=record.n_inb;
    }
    public static AHT sum(AHT...arr){
        AHT aht = new AHT();
        for (var a:arr){
            aht.t_ring+=a.t_ring;
            aht.t_inb+=a.t_inb;
            aht.t_hold+=a.t_hold;
            aht.t_acw+=a.t_acw;
            aht.n_inb+=a.n_inb;
            aht.recordsCount+=a.recordsCount;
            aht.currentThreadNames.addAll(a.currentThreadNames);
        }
        return aht;
    }
    public long aht() {
        return (t_ring + t_inb + t_hold + t_acw) / n_inb;
    }
}
