package com.stock.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.model.Accuweather;
import com.stock.repositories.AccuweatherRepository;

@Service
public class AccuweatherService {

    private final AccuweatherRepository repository;

    public AccuweatherService(AccuweatherRepository repository) {
        this.repository = repository;
    }

    public List<Accuweather> getRecordsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findAllByCreatedOnBetween(startDate, endDate);
    }
}

