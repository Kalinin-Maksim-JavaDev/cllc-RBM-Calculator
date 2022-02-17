package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class AHT extends GenericIndicator {

    public static AtomicLong summingTime = new AtomicLong();
    public static AtomicLong aggregateTime = new AtomicLong();
    long recordsCount;
    Set<String> currentThreadNames = new HashSet<>();
    long t_ring, t_inb, t_hold, t_acw, n_inb;
    long value;

    @Override
    public String toString() {
        return "AHT{" +
                "recordsCount=" + recordsCount +
                ", aht=" + value() +
                ", currentThreadNames=" + currentThreadNames +
                '}';
    }

    @Override
    public void add(Record record){
        long start = System.currentTimeMillis();;
        recordsCount++;
        currentThreadNames.add(Thread.currentThread().getName()
                .replace("ForkJoinPool.commonPool-worker-", "")
                .replace("ForkJoinPool-1-worker-", ""));
        t_ring += record.t_ring;
        t_inb += record.t_inb;
        t_hold += record.t_hold;
        t_acw += record.t_acw;
        n_inb += record.n_inb;
        summingTime.addAndGet(System.currentTimeMillis() - start);
    }

    @Override
    public void add(GenericIndicator other) {
        long start = System.currentTimeMillis();
        ;
        t_ring += ((AHT) other).t_ring;
        t_inb += ((AHT) other).t_inb;
        t_hold += ((AHT) other).t_hold;
        t_acw += ((AHT) other).t_acw;
        n_inb += ((AHT) other).n_inb;
        recordsCount += ((AHT) other).recordsCount;
        currentThreadNames.addAll(((AHT) other).currentThreadNames);
        aggregateTime.addAndGet(System.currentTimeMillis() - start);
    }

    @Override
    public long value() {
        try {
            return (t_ring + t_inb + t_hold + t_acw) / n_inb;
        } catch (ArithmeticException ex) {
            return 0;
        }
    }
}
