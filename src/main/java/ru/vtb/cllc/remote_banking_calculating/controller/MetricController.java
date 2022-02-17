package ru.vtb.cllc.remote_banking_calculating.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.AHT;
import ru.vtb.cllc.remote_banking_calculating.service.MetricService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class MetricController<T> {

    MetricService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseEntity<Map<T, AHT>> getAHT(@RequestParam Long id_user) {

        service.getAHT(id_user);

        return new ResponseEntity(null, HttpStatus.OK);
    }
}
