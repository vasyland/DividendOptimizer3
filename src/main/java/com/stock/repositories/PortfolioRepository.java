package com.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.Portfolio;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // Find all portfolios by userId
    List<Portfolio> findByUserId(Long userId);
    
 // Find a specific portfolio by its ID
    Optional<Portfolio> findById(Long id);
    
    // Find a specific portfolio by its ID and the userId to ensure it's the user's portfolio
    Optional<Portfolio> findByIdAndUserId(Long id, Long userId);
    
    // Check if a portfolio with a given name exists for a specific user
    boolean existsByNameAndUserId(String name, Long userId);
}
