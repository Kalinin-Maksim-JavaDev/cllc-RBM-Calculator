package ru.vtb.cllc.remote_banking_calculating;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCase;
import ru.vtb.cllc.remote_banking_calculating.dao.redis.ShowCaseRepository;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
public class LoadToRedis {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        ShowCaseRepository showCaseRepository = context.getBean(ShowCaseRepository.class);

        try {
            List<File> files = Files.list(Path.of("C:\\Work\\Liga\\VTB\\cllc\\showcase"))
                    .map(Path::toFile).collect(Collectors.toList());
            files.stream()
                    .parallel()
                    .forEach(file -> {
                        showCaseRepository.put(file.getName().replace(".txt", ""), new ShowCase(records(file)));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.close();
    }

    private static Record[] records(File file) {
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
        return records.orElse(new Record[0]);
    }
}
