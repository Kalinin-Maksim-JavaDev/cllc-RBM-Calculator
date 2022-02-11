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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
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
    public ResponseEntity<Map<LocalDate, AHT>> getAHT(@RequestParam Long id_user) {

        long readingStart = System.currentTimeMillis();
        List<Record> records = List.of();
        try {
            List<File> files = Files.list(Path.of("C:\\Work\\Liga\\VTB\\cllc\\showcase"))
                    .map(Path::toFile).collect(Collectors.toList());
            records =  files.stream()
                    .parallel()
                    .flatMap(this::records).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf( "%,d sec for reading", (System.currentTimeMillis() - readingStart)/1000);
        System.out.println();

        long start = System.currentTimeMillis();
        Map<LocalDate, AHT> byDate = new HashMap<>();
        records.stream()
                .filter(record -> Objects.isNull(id_user) || record.id_user==id_user)
                .collect(Collectors.groupingBy(Record::getDate)).forEach((date, recs) -> {
                    AHT ahtTask = recs.stream().parallel().collect(Collector.of(AHT::new, AHT::add, AHT::sum, Function.identity()));
                    byDate.put(date, ahtTask);
                });
        System.out.printf("%,d sec for calculating", (System.currentTimeMillis() - start)/1000);
        System.out.println();
        System.out.printf("%,d operations", AHT.globalCounter.get());
        System.out.println();

        return new ResponseEntity<>(byDate,  HttpStatus.OK);
    }

    private Stream<Record> records(File file) {
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
        MetricController controller = new MetricController();
        System.out.println(controller.getAHT(null));

    }
}
