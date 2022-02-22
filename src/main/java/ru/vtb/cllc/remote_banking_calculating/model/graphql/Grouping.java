package ru.vtb.cllc.remote_banking_calculating.model.graphql;

import com.google.common.base.Function;
import org.springframework.util.StringUtils;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public enum Grouping {
    BY_MONTH(Record::getEpochMonthFirstDay) {
        @Override
        public String description(int part) {
            return LocalDate.ofEpochDay(part).format(DateTimeFormatter.ofPattern("LLLL (YYYY)"));
        }
    },
    BY_DAY(Record::getEpochDay) {
        @Override
        public String description(int part) {
            return LocalDate.ofEpochDay(part).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    },
    BY_DIVISION(Record::getId_tree_division),
    BY_REGION(Record::getId_region),
    BY_USER(Record::getId_user),
    BY_LINE(Record::getId_line);
    private final Function<Record, Integer> classifier;
    private final String name = StringUtils.capitalize(name().replace("BY_", "").toLowerCase(Locale.ROOT));

    Grouping(Function<Record, Integer> classifier) {
        this.classifier = classifier;
    }

    public Function<Record, Integer> getClassifier() {
        return classifier;
    }

    public String description(int part) {
        return name.concat(": ").concat(String.valueOf(part));
    }
}
