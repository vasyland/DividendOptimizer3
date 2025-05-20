package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.model.MoneyTransfer;

public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
    List<MoneyTransfer> findByPortfolioId(Long portfolioId);
}
