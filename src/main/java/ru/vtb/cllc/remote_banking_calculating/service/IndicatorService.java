package ru.vtb.cllc.remote_banking_calculating.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCase;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCaseRepository;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.CodeGenerator;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class IndicatorService {

    private ShowCaseRepository showCaseRepository;

    private List<Record> records;

    final private IndicatorRegistry indicatorRegistry;

    @Autowired
    public IndicatorService(ShowCaseRepository showCaseRepository, IndicatorRegistry indicatorRegistry) {
        this.showCaseRepository = showCaseRepository;
        this.indicatorRegistry = indicatorRegistry;
    }

    public IndicatorService(List<Record> records, IndicatorRegistry indicatorRegistry) {
        this.records = records;
        this.indicatorRegistry = indicatorRegistry;
    }

    public <T, E> Map<T, Map<String, E>> calculate(Long id_user, String indicatorNames, Function<Record, T> demension) {

        List<ShowCase> showCases = showCaseRepository.get(LocalDate.of(2021, 12, 29));

        Stream<Record> recordStream = Arrays.stream(showCases.get(0).getRecords());

        List<Indicator<E>> indicators = indicatorRegistry.get(indicatorNames);

        Map<T, Map<String, E>> group = recordStream
                .filter(record -> Objects.isNull(id_user) || record.id_user == id_user)
                .collect(groupingBy(demension, collectingAndThen(reducing(new Record(), Record::sum), record ->
                        indicators.stream().collect(toMap(Object::toString, indicator -> indicator.apply(record)))
                )));

        return group;
    }

    private static Stream<Record> records(File file) {
        String jsonArray = null;
        try {
            jsonArray = new BufferedReader(new FileReader(file)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        Optional<Record[]> records = null;
        try {
            records = Optional.ofNullable(objectMapper.readValue(jsonArray, Record[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return records.map(Arrays::stream).orElse(Stream.empty());
    }

    public static void main(String[] args) {

        long readingStart = System.currentTimeMillis();
        List<Record> records = List.of();
        try {
            List<File> files = Files.list(Path.of("C:\\Work\\Liga\\VTB\\cllc\\showcase"))
                    .map(Path::toFile).collect(Collectors.toList());
            records = files.stream()
                    .parallel()
                    .flatMap(IndicatorService::records).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("%,d sec for reading", (System.currentTimeMillis() - readingStart) / 1000);
        System.out.println();

        long minCountId = records.stream()
                .collect(groupingBy(Record::getId_user, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .get();
        System.out.printf("%d min count id", minCountId);
        System.out.println();
        long maxCountId = records.stream()
                .collect(groupingBy(Record::getId_user, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> (int) -(e1.getValue() - e2.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .get();
        System.out.printf("%d max count id", maxCountId);
        System.out.println();

        {
            System.out.println("----------------------");
            System.out.println("by month: ");
            IndicatorService controller = new IndicatorService(records, new IndicatorRegistry(new CodeGenerator()));

            {
                var aht = controller.calculate(null, "AHT", Record::getMonth);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.calculate(minCountId, "AHT", Record::getMonth);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.calculate(maxCountId, "AHT", Record::getMonth);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
        }
        {
            System.out.println("----------------------");
            System.out.println("by date: ");
            IndicatorService controller = new IndicatorService(records, new IndicatorRegistry(new CodeGenerator()));

            {
                var aht = controller.calculate(null, "AHT", Record::getEpochDay);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.calculate(minCountId, "AHT", Record::getEpochDay);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.calculate(maxCountId, "AHT", Record::getEpochDay);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
        }


    }
}
