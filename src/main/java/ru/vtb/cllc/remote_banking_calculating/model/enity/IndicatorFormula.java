package ru.vtb.cllc.remote_banking_calculating.model.enity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
public class IndicatorFormula {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;
    @Embedded
    private Period period;
    private String expression;

    @Embeddable
    private static class Period {
        @Column(name = "pBegin")
        private LocalDate begin;
        @Column(name = "pEnd")
        private LocalDate end;
    }
}
