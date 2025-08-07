package com.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.data.CompanyPriceProjection;
import com.stock.repositories.CompanyPriceRepository;

@Service
public class CompanyPriceService {

    private final CompanyPriceRepository repository;

    public CompanyPriceService(CompanyPriceRepository repository) {
        this.repository = repository;
    }

    public List<CompanyPriceProjection> getLatestPrices() {
        return repository.getLatestCompanyPrices();
    }
}
