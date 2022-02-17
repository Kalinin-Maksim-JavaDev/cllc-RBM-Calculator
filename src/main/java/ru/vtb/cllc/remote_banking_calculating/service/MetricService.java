package ru.vtb.cllc.remote_banking_calculating.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.CodeGenerator;
import ru.vtb.cllc.remote_banking_calculating.model.calculating.IndicatorRegistry;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.AHT;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.GenericIndicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class MetricService<T> {

    final private List<Record> records;
    final private Function<Record, T> demension;
    final private boolean paralleled = true;

    final private IndicatorRegistry indicatorRegistry;

    public Map<T, GenericIndicator> getAHT(Long id_user) {

        AHT.summingTime.set(0);
        AHT.aggregateTime.set(0);

        System.out.printf("id: %d ", id_user);
        System.out.println();

        long start = System.currentTimeMillis();
        Stream<Record> recordStream = records.stream();
        if (paralleled) recordStream = recordStream.parallel();
        ForkJoinPool forkJoinPool = new ForkJoinPool(20);

        GenericIndicator indicator = indicatorRegistry.get("AHT");

        Stream<Record> finalRecordStream = recordStream;
        Map<T, GenericIndicator> group = null;
        try {
            group = forkJoinPool.submit(() ->
                            finalRecordStream.filter(record -> Objects.isNull(id_user) || record.id_user == id_user)
                                    .collect(Collectors.groupingByConcurrent(demension,
                                            Collector.of(() -> indicator, (GenericIndicator ind, Record record) -> ind.add(record), GenericIndicator::sum, Function.identity()))))
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        System.out.printf("%,d millisec for calculating", (System.currentTimeMillis() - start));
        System.out.println();

        System.out.printf("summingTime: %,d ", AHT.summingTime.get());
        System.out.println();
        System.out.printf("aggregateTime: %,d ", AHT.aggregateTime.get());
        System.out.println();
        return group;
        //return new ResponseEntity<>(byDate,  HttpStatus.OK);
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
                    .flatMap(MetricService::records).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("%,d sec for reading", (System.currentTimeMillis() - readingStart) / 1000);
        System.out.println();

        long minCountId = records.stream()
                .collect(Collectors.groupingBy(Record::getId_user, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .get();
        System.out.printf("%d min count id", minCountId);
        System.out.println();
        long maxCountId = records.stream()
                .collect(Collectors.groupingBy(Record::getId_user, Collectors.counting()))
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
            MetricService controller = new MetricService<>(records, Record::getMonth, new IndicatorRegistry(new CodeGenerator()));

            {
                var aht = controller.getAHT(null);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.getAHT(minCountId);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.getAHT(maxCountId);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
        }
        {
            System.out.println("----------------------");
            System.out.println("by date: ");
            MetricService controller = new MetricService<>(records, Record::getEpochDay, new IndicatorRegistry(new CodeGenerator()));

            {
                var aht = controller.getAHT(null);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.getAHT(minCountId);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
            {
                var aht = controller.getAHT(maxCountId);
                System.out.printf("aht: %s ", aht);
                System.out.println();
            }
        }


    }
}
