package com.stock.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.MoneyTransfer;
import com.stock.services.MoneyTransferService;

@RestController
@RequestMapping("/api/money-transfers")
public class MoneyTransferController {

    private final MoneyTransferService service;

    public MoneyTransferController(MoneyTransferService service) {
        this.service = service;
    }

    @GetMapping
    public List<MoneyTransfer> getAll() {
        return service.getAllTransfers();
    }

    @GetMapping("/portfolio/{portfolioId}")
    public List<MoneyTransfer> getByPortfolio(@PathVariable("portfolioId") Long portfolioId) {
        return service.getTransfersByPortfolioId(portfolioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyTransfer> getById(@PathVariable("id") Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MoneyTransfer create(@RequestBody MoneyTransfer transfer) {
        return service.createTransfer(transfer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
}
