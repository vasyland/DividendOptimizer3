package com.stock.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.Accuweather;
import com.stock.services.AccuweatherService;

/**
 * Example: GET https://localhost:8081/api/weather/range?start=2025-09-01&end=2025-09-13
 */
@RestController
@RequestMapping("/api/weather")
public class AccuweatherController {

    private final AccuweatherService service;

    public AccuweatherController(AccuweatherService service) {
        this.service = service;
    }

    @GetMapping("/range")
    public List<Accuweather> getWeatherInRange(
    		@RequestParam("start") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return service.getRecordsBetween(start.atStartOfDay(),
                end.atTime(23, 59, 59));
    }
}
