package com.stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

	// Find all trades for a specific transaction
    List<Transaction> findByPortfolioId(Long portfolioId);
    
 // Find a specific trade by its ID
    Optional<Transaction> findById(Long id);
    
    // Find a specific trade by its ID and the portfolioId to ensure it's the correct portfolio
    Optional<Transaction> findByIdAndPortfolioId(Long id, Long portfolioId);

 // Find transactions for a specific portfolio and symbol
    List<Transaction> findByPortfolioIdAndSymbol(Long portfolioId, String symbol);
}
