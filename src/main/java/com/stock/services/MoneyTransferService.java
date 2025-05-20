package com.stock.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.model.MoneyTransfer;
import com.stock.repositories.MoneyTransferRepository;

@Service
public class MoneyTransferService {

    private final MoneyTransferRepository repository;

    public MoneyTransferService(MoneyTransferRepository repository) {
        this.repository = repository;
    }

    public List<MoneyTransfer> getAllTransfers() {
        return repository.findAll();
    }

    public List<MoneyTransfer> getTransfersByPortfolioId(Long portfolioId) {
        return repository.findByPortfolioId(portfolioId);
    }

    public Optional<MoneyTransfer> getById(Long id) {
        return repository.findById(id);
    }

    public MoneyTransfer createTransfer(MoneyTransfer transfer) {
        return repository.save(transfer);
    }

    public void deleteTransfer(Long id) {
        repository.deleteById(id);
    }
}
