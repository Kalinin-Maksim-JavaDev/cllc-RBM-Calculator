package ru.vtb.cllc.remote_banking_calculating.model.indicator;

import lombok.AllArgsConstructor;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.util.Collection;
import java.util.stream.Collectors;

public class AHT {

    long t_ring,t_inb,t_hold,t_acw,n_inb;

    public void add(Record record){
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
        }
        return aht;
    }
    public long aht() {
        System.out.println(Thread.currentThread().getName());
        return (t_ring + t_inb + t_hold + t_acw) / n_inb;
    }
}
