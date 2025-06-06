package com.stock.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioSummary;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.PortfolioSummaryRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncrementalPortfolioSummaryUpdater {
	
	private static final Logger log = LoggerFactory.getLogger(IncrementalPortfolioSummaryUpdater.class);


    private final PortfolioRepository portfolioRepository;
    private final PortfolioSummaryRepository portfolioSummaryRepository;
    private final HoldingRepository holdingRepository;
    
    
    
    public IncrementalPortfolioSummaryUpdater(PortfolioRepository portfolioRepository,
			PortfolioSummaryRepository portfolioSummaryRepository, HoldingRepository holdingRepository) {
		super();
		this.portfolioRepository = portfolioRepository;
		this.portfolioSummaryRepository = portfolioSummaryRepository;
		this.holdingRepository = holdingRepository;
	}

	/**
     * Apply a new transaction's effect on cash and realized PnL
     */
    @Transactional
    public void applyTransaction(Transaction tx) {
    	
        Portfolio portfolio = tx.getPortfolio();
        
     // Use latest persisted portfolio for safety
        portfolio = portfolioRepository.findById(portfolio.getId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        
        BigDecimal currentCash = portfolio.getCurrentCash() != null ? portfolio.getCurrentCash() : BigDecimal.ZERO;
        
        PortfolioSummary summary = portfolioSummaryRepository.findById(portfolio.getId())
                .orElseThrow(() -> new RuntimeException("Portfolio summary not found"));

        BigDecimal txValue = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()));
        BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;

        if (tx.getTransactionType() == TransactionType.BUY) {
            
        	// Deduct total cost
            BigDecimal totalCost = txValue.add(commissions);
            portfolio.setCurrentCash(currentCash.subtract(totalCost));
            summary.setCash(summary.getCash().subtract(totalCost));
            
        } else if (tx.getTransactionType() == TransactionType.SELL) {
            
        	// Add proceeds and compute realized PnL
            BigDecimal totalProceeds = txValue.subtract(commissions);
            portfolio.setCurrentCash(currentCash.add(totalProceeds));
            summary.setCash(summary.getCash().add(totalProceeds));
            
            summary.setRealizedPnL(tx.getRealizedPnl() != null ? summary.getRealizedPnL().add(tx.getRealizedPnl()) : summary.getRealizedPnL());
            log.info("[IncrementalPortfolioSummaryUpdater:applyTransaction] Realized PnL: {}", summary.getRealizedPnL());
            
            // Estimate realized PnL using average cost (optional: pass avg cost as parameter)
//            Optional<Holding> holdingOpt = holdingRepository.findByPortfolioIdAndSymbol(portfolio.getId(), tx.getSymbol());
//
//            if (holdingOpt.isPresent()) {
//            	
//                Holding holding = holdingOpt.get();
//                BigDecimal avgCost = holding.getAvgCostPerShare();
//                BigDecimal costBasis = avgCost.multiply(BigDecimal.valueOf(tx.getShares()));
//                BigDecimal pnl = txValue.subtract(costBasis).subtract(commissions);
//                BigDecimal realized = summary.getRealizedPnL() != null ? summary.getRealizedPnL() : BigDecimal.ZERO;
//                summary.setRealizedPnL(realized.add(pnl));
//            }
            
//            holdingOpt.ifPresent(holding -> {
//                BigDecimal avgCost = holding.getAvgCostPerShare();
//                BigDecimal costBasis = avgCost.multiply(BigDecimal.valueOf(tx.getShares()));
//                BigDecimal pnl = txValue.subtract(costBasis).subtract(commissions);
//                BigDecimal realized = summary.getRealizedPnL() != null ? summary.getRealizedPnL() : BigDecimal.ZERO;
//                summary.setRealizedPnL(realized.add(pnl));
//            });

        }

        portfolioRepository.save(portfolio);
        
        log.info("[IncrementalPortfolioSummaryUpdater:applyTransaction] Updated portfolio ID: {}", portfolio.getId());
        summary.setUpdatedAt(LocalDateTime.now());
        portfolioSummaryRepository.save(summary);
    }

    /**
     * Revert an existing transaction's effect (e.g., for delete or edit)
     */
    @Transactional
    public void revertTransaction(Transaction tx) {
        Transaction reversed = new Transaction();
        reversed.setPortfolio(tx.getPortfolio());
        reversed.setSymbol(tx.getSymbol());
        reversed.setShares(tx.getShares());
        reversed.setPrice(tx.getPrice());
        reversed.setCommissions(tx.getCommissions());
        reversed.setTransactionType(tx.getTransactionType() == TransactionType.BUY
                ? TransactionType.SELL
                : TransactionType.BUY);
        applyTransaction(reversed);
    }

    /**
     * For edits: revert old transaction, then apply new one.
     */
    @Transactional
    public void updateTransaction(Transaction oldTx, Transaction newTx) {
        revertTransaction(oldTx);
        applyTransaction(newTx);
    }
}
