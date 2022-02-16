package ru.vtb.cllc.remote_banking_calculating.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.AHT;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class MetricController<T> {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseEntity<Map<T, AHT>> getAHT(@RequestParam Long id_user) {

        return new ResponseEntity(null, HttpStatus.OK);
    }
}
