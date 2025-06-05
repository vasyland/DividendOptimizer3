package com.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.model.PortfolioSummary;

public interface PortfolioSummaryRepository extends JpaRepository<PortfolioSummary, Long> {

	void deleteByPortfolioId(Long portfolioId);
}