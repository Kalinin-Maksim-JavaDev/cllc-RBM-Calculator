package ru.vtb.cllc.remote_banking_calculating.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.AHT;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/login")
public class MetricController {

    final private List<Record> records;

    public MetricController(List<Record> records) {
        this.records = records;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseEntity<Map<LocalDate, AHT>> getAHT(@RequestParam Long id_user) {

        System.out.printf("id: %d ", id_user);
        System.out.println();

        long start = System.currentTimeMillis();
        Map<LocalDate, AHT> byDate = new HashMap<>();
        records.stream()
                .filter(record -> Objects.isNull(id_user) || record.id_user==id_user)
                .collect(Collectors.groupingBy(Record::getDate)).forEach((date, recs) -> {
                    AHT ahtTask = recs.stream()
                            .parallel()
                            .collect(Collector.of(AHT::new, AHT::add, AHT::sum, Function.identity()));
                    byDate.put(date, ahtTask);
                });
        System.out.printf("%,d millisec for calculating", (System.currentTimeMillis() - start));
        System.out.println();


        return new ResponseEntity<>(byDate,  HttpStatus.OK);
    }

    private static Stream<Record> records(File file) {
        System.out.println(Thread.currentThread().getName());
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
            records =  files.stream()
                    .parallel()
                    .flatMap(MetricController::records).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf( "%,d sec for reading", (System.currentTimeMillis() - readingStart)/1000);
        System.out.println();

        long minCountId = records.stream()
                .collect(Collectors.groupingBy(Record::getId_user, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1,e2)-> (int) (e1.getValue()-e2.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .get();
        System.out.printf( "%d min count id", minCountId);
        System.out.println();
        long maxCountId = records.stream()
                .collect(Collectors.groupingBy(Record::getId_user, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> (int) -(e1.getValue() - e2.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .get();
        System.out.printf( "%d max count id", maxCountId);
        System.out.println();

        MetricController controller = new MetricController(records);

        {
            ResponseEntity<Map<LocalDate, AHT>> aht = controller.getAHT(minCountId);
            System.out.printf("aht: %s ", aht);
            System.out.println();
        }
        {
            ResponseEntity<Map<LocalDate, AHT>> aht = controller.getAHT(maxCountId);
            System.out.printf("aht: %s ", aht);
            System.out.println();
        }

        System.out.printf("summingTime: %,d ", AHT.summingTime.get());
        System.out.println();
        System.out.printf("aggregateTime: %,d ", AHT.aggregateTime.get());
        System.out.println();
    }
}
