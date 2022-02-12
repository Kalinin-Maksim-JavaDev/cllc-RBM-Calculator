package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import lombok.AllArgsConstructor;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AHT {

    public static AtomicLong summingTime = new AtomicLong();
    public static AtomicLong aggregateTime = new AtomicLong();
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
        long start = System.currentTimeMillis();;
        recordsCount++;
        currentThreadNames.add(Thread.currentThread().getName()
                .replace("ForkJoinPool.commonPool-worker-", "")
                .replace("ForkJoinPool-1-worker-", ""));
        t_ring+=record.t_ring;
        t_inb+=record.t_inb;
        t_hold+=record.t_hold;
        t_acw+=record.t_acw;
        n_inb+=record.n_inb;
        summingTime.addAndGet(System.currentTimeMillis() - start);
    }
    public static AHT sum(AHT...arr){
        long start = System.currentTimeMillis();;
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
        aggregateTime.addAndGet(System.currentTimeMillis() - start);
        return aht;
    }
    public long aht() {
        if (n_inb==0) return 0;
        return (t_ring + t_inb + t_hold + t_acw) / n_inb;
    }
}
