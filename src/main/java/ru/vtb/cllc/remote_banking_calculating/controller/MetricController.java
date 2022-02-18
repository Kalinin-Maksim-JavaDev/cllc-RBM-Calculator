package ru.vtb.cllc.remote_banking_calculating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.GenericIndicator;
import ru.vtb.cllc.remote_banking_calculating.service.MetricService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/indicator")
@RequiredArgsConstructor
public class MetricController<T> {

    private final MetricService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseEntity<Map<T, GenericIndicator>> getAHT(@RequestParam Long id_user) {

        Map<Integer, GenericIndicator> aht = service.getAHT(id_user, Record::getMonth);

        return new ResponseEntity(aht, HttpStatus.OK);
    }
}
