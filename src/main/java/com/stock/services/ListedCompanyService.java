package com.stock.services;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stock.data.ListedCompanyDto;
import com.stock.model.ListedCompany;
import com.stock.repositories.ListedCompanyRepository;

@Service
public class ListedCompanyService {

    private final ListedCompanyRepository listedCompanyRepository;

    public ListedCompanyService(ListedCompanyRepository listedCompanyRepository) {
        this.listedCompanyRepository = listedCompanyRepository;
    }
    
    public List<ListedCompanyDto> getAllCompanies() {
        return listedCompanyRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ListedCompanyDto> searchBySymbolPrefix(String prefix) {
        return listedCompanyRepository.findBySymbolStartingWithIgnoreCase(prefix)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ListedCompanyDto> searchByNamePrefix(String prefix) {
        return listedCompanyRepository.findByNameStartingWithIgnoreCase(prefix)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ListedCompanyDto> searchBySymbolOrName(String query) {
        return listedCompanyRepository.searchBySymbolOrName(query)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }    

    private ListedCompanyDto convertToDto(ListedCompany company) {
        return new ListedCompanyDto(
                company.getSymbol(),
                company.getName(),
                company.getExchange(),
                formatMarketCap(company.getMarketcap())
        );
    }

    private String formatMarketCap(Long marketcap) {
        if (marketcap == null) {
            return "-";
        }
        if (marketcap >= 1_000_000_000_000L) {
            return String.format("%.1fT", marketcap / 1_000_000_000_000.0);
        } else if (marketcap >= 1_000_000_000L) {
            return String.format("%.1fB", marketcap / 1_000_000_000.0);
        } else if (marketcap >= 1_000_000L) {
            return String.format("%.1fM", marketcap / 1_000_000.0);
        } else if (marketcap >= 1_000L) {
            return String.format("%.1fK", marketcap / 1_000.0);
        } else {
            return String.valueOf(marketcap);
        }
    }
}
