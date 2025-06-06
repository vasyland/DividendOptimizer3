package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.model.Holding;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.HoldingRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service to manage incremental updates to holdings based on transactions.
 * This service handles the logic for applying, reversing, and updating
 * transactions
 * in a way that maintains the integrity of the holdings data.
 * Usage: 
 * Create Transaction: incrementalHoldingsService.applyNewTransaction(tx);
 * Edit Transaction: incrementalHoldingsService.updateTransaction(oldTx, newTx);
 * Reverse/Delete Transaction: incrementalHoldingsService.reverseTransaction(tx);
 */

@Service
public class IncrementalHoldingsService {
	
	private static final Logger log = LoggerFactory.getLogger(IncrementalHoldingsService.class);

    private final HoldingRepository holdingRepository;
    
    public IncrementalHoldingsService(HoldingRepository holdingRepository) {
		super();
		this.holdingRepository = holdingRepository;
	}

	@Transactional
    public void applyNewTransaction(Transaction tx) {
        adjustHolding(tx, true);
    }

    @Transactional
    public void reverseTransaction(Transaction tx) {
        adjustHolding(tx, false);
    }

    @Transactional
    public void updateTransaction(Transaction oldTx, Transaction newTx) {
        reverseTransaction(oldTx);
        applyNewTransaction(newTx);
    }

    private void adjustHolding(Transaction tx, boolean apply) {
        String symbol = tx.getSymbol();
        Long portfolioId = tx.getPortfolio().getId();
        int txShares = tx.getShares();
        BigDecimal price = tx.getPrice();
        BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
        BigDecimal tradeCost = price.multiply(BigDecimal.valueOf(txShares)).add(commissions);
        
        Optional<Holding> existingOpt = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);

        int sign = apply ? 1 : -1;

        if (tx.getTransactionType() == TransactionType.BUY) {
            if (existingOpt.isPresent()) {
                Holding h = existingOpt.get();
                int totalShares = h.getShares() + sign * txShares;
                BigDecimal newBookCost = h.getBookCost().add(tradeCost.multiply(BigDecimal.valueOf(sign)));

                if (totalShares < 0) throw new IllegalStateException("Negative share count not allowed");

                if (totalShares == 0) {
                    holdingRepository.delete(h);
                } else {
                    h.setShares(totalShares);
                    h.setBookCost(newBookCost);
                    BigDecimal avgCost = newBookCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
                    h.setAvgCostPerShare(avgCost);
                    holdingRepository.save(h);
                }
            } else if (apply) {
                // Only create on apply, not on reverse
                Holding h = new Holding();
                h.setPortfolio(tx.getPortfolio());
                h.setSymbol(symbol);
                h.setShares(txShares);
                h.setBookCost(tradeCost.setScale(2, RoundingMode.HALF_UP));
                h.setAvgCostPerShare(price.setScale(2, RoundingMode.HALF_UP));
                h.setCurrency(tx.getCurrency());
                holdingRepository.save(h);
            }
            
        } else if (tx.getTransactionType() == TransactionType.SELL) {
        	
            if (existingOpt.isPresent()) {
            
            	Holding h = existingOpt.get();
            	
                int adjustedShares = h.getShares() - sign * txShares; // reverse: +, apply: -
                if (adjustedShares < 0) throw new IllegalStateException("Negative share count not allowed");

                if (adjustedShares == 0) {
                	
                    holdingRepository.delete(h);
                } else {
                	
                    BigDecimal costReduction = h.getAvgCostPerShare().multiply(BigDecimal.valueOf(txShares)).multiply(BigDecimal.valueOf(sign));
                    BigDecimal newBookCost = h.getBookCost().add(costReduction); // reverse = +cost, apply = -cost
                    BigDecimal realizedPnL =  h.getRealizedPnL() != null ? h.getRealizedPnL().add(tx.getRealizedPnl()) : tx.getRealizedPnl();
                    
					log.info(
							"[IncrementalHoldingsService:adjustHolding] Adjusting holding for symbol: {}, portfolioId: {}, shares: {}, newBookCost: {}, realizedPnL: {}",
							symbol, portfolioId, adjustedShares, newBookCost, realizedPnL);
                    
                    h.setShares(adjustedShares);
                    h.setBookCost(newBookCost);
                    h.setRealizedPnL(realizedPnL);
                    
                    holdingRepository.save(h);
                }
            } else if (apply) {
                throw new IllegalStateException("Cannot sell non-existent holding");
            }
        }
    }
}
