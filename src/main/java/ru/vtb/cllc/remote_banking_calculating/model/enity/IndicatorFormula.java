package ru.vtb.cllc.remote_banking_calculating.model.enity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Data
public class IndicatorFormula {

    @Id
    private String name;
    private String expression;
    @Embedded
    private Period period;

    @Embeddable
    private static class Period {
        @Column(name = "pBegin")
        private LocalDate begin;
        @Column(name = "pEnd")
        private LocalDate end;
    }
}
