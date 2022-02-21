package ru.vtb.cllc.remote_banking_calculating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/indicator")
@RequiredArgsConstructor
public class IndicatorController<T> {

    private final IndicatorService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/byMonth")
    public ResponseEntity getIndicators(Integer id_user, String indicators) {

        Map<Integer, Map<String, Object>> group = service.calculate(id_user,
                Record::getMonth,
                Arrays.stream(indicators.split(",")).collect(Collectors.toList()));

        return new ResponseEntity(group, HttpStatus.OK);
    }
}
