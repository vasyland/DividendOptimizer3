package com.stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.Holding;

import jakarta.transaction.Transactional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    // Find all holdings for a specific portfolio
    List<Holding> findByPortfolioId(Long portfolioId);

    // Find a specific holding by portfolio and symbol
    Optional<Holding> findByPortfolioIdAndSymbol(Long portfolioId, String symbol);

    // Check if a holding exists for a portfolio and symbol
    boolean existsByPortfolioIdAndSymbol(Long portfolioId, String symbol);

    // Delete all holdings for a specific portfolio (if needed for cleanup)
    @Modifying
    @Transactional
    @Query("DELETE FROM Holding h WHERE h.portfolio.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Long portfolioId);
    
 // ✅ Add this method to delete holding for a specific symbol in a portfolio
    void deleteByPortfolioIdAndSymbol(Long portfolioId, String symbol);
    
    @Override
    @Transactional
    void flush();
    
    
    // Find all holdings for a specific user
    @Query("SELECT h FROM Holding h WHERE h.portfolio.user.id = :userId")
    List<Holding> findByUserId(@Param("userId") Long userId);
}
