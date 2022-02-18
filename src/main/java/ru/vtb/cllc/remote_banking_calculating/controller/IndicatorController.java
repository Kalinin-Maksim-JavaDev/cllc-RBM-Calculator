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
import ru.vtb.cllc.remote_banking_calculating.model.indicator.GenericIndicator;
import ru.vtb.cllc.remote_banking_calculating.service.IndicatorService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/indicator")
@RequiredArgsConstructor
public class IndicatorController<T> {

    private final IndicatorService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/byMonth")
    public ResponseEntity<Map<T, GenericIndicator>> getIndicators(Long id_user, String indicators) {

        Map<Integer, GenericIndicator> group = service.calculate(id_user, indicators, Record::getMonth);

        return new ResponseEntity(group, HttpStatus.OK);
    }
}
