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
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/login")
public class MetricController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseEntity<Map<LocalDate, AHT>> getAHT(@RequestParam long id_user) {

        Stream<Record> recordStream = Stream.empty();
        try {
            recordStream = Files.list(Path.of("C:\\Work\\Liga\\VTB\\cllc\\showcase"))
                    .flatMap(this::records);

        } catch (IOException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        Map<LocalDate, AHT> byDate = new HashMap<>();
        recordStream
                .filter(record -> Objects.isNull(id_user) || record.id_user==id_user)
                .collect(Collectors.groupingBy(Record::getDate)).forEach((date, records) -> {
                    ForkJoinPool forkJoinPool = new ForkJoinPool(8);
                    forkJoinPool.submit(()-> {
                            byDate.put(date, records.stream().parallel().collect(Collector.of(AHT::new, AHT::add, AHT::sum, Function.identity(), Collector.Characteristics.CONCURRENT)));
                    });
                });
        System.out.printf( "%,d sec", (System.currentTimeMillis() - start)/1000);
        System.out.println();

        return new ResponseEntity<>(byDate,  HttpStatus.OK);
    }

    private Stream<Record> records(Path path) {
        String jsonArray = null;
        try {
            jsonArray = new BufferedReader(new FileReader(path.toFile())).readLine();
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
        MetricController controller = new MetricController();
        System.out.println(controller.getAHT(521));

    }
}
