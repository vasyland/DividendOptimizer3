package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.data.PortfolioSummaryDTO;
import com.stock.model.CurrentPrice;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.CurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortfolioSummaryService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private CurrentPriceRepository currentPriceRepository;

    public PortfolioSummaryDTO calculatePortfolioSummary(Long portfolioId) {
    	
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        BigDecimal currentCash = portfolio.getCurrentCash();
        
        log.info("#1 [PortfolioSummaryService:calculatePortfolioSummary] Current Cash: {}", currentCash);
        
        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);
        
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);

        BigDecimal realizedPnL = BigDecimal.ZERO;
        BigDecimal unrealizedPnL = BigDecimal.ZERO;
        BigDecimal totalMarketValue = BigDecimal.ZERO;

        // Group all buy transactions by symbol to estimate avg cost for realized PnL calc
        Map<String, BigDecimal> avgCosts = holdings.stream()
                .collect(Collectors.toMap(Holding::getSymbol, Holding::getAvgCostPerShare));

        log.info("#2 [PortfolioSummaryService:calculatePortfolioSummary] Holdings size: {}", holdings.size());
        // Compute realized PnL
        for (Transaction tx : transactions) {
            if (tx.getTransactionType() == TransactionType.SELL) {
                BigDecimal proceeds = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()))
                        .subtract(tx.getCommissions());
                BigDecimal costBasis = avgCosts.getOrDefault(tx.getSymbol(), BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(tx.getShares()));
                realizedPnL = realizedPnL.add(proceeds.subtract(costBasis));
            }
        }

        log.info("#3 [PortfolioSummaryService:calculatePortfolioSummary] Realized PnL: {}", realizedPnL);
        
        // Compute unrealized PnL and market value
        for (Holding h : holdings) {
            Optional<CurrentPrice> maybePrice = Optional.ofNullable(currentPriceRepository.findBySymbol(h.getSymbol()).get(0));
            if (maybePrice.isPresent()) {
                BigDecimal marketPrice = maybePrice.get().getPrice();
                BigDecimal marketValue = marketPrice.multiply(BigDecimal.valueOf(h.getShares()));
                totalMarketValue = totalMarketValue.add(marketValue);

                BigDecimal bookCost = h.getBookCost();
                unrealizedPnL = unrealizedPnL.add(marketValue.subtract(bookCost));
            }
        }

        BigDecimal totalValue = currentCash.add(totalMarketValue);
        log.info("#4 [PortfolioSummaryService:calculatePortfolioSummary] Total Market Value: {}", totalMarketValue);

        PortfolioSummaryDTO ps = new PortfolioSummaryDTO();
        ps.setPortfolioId(portfolioId);
        ps.setCash(currentCash.setScale(2, RoundingMode.HALF_UP));
        ps.setMarketValue(totalMarketValue.setScale(2, RoundingMode.HALF_UP));
        ps.setTotalValue(totalValue.setScale(2, RoundingMode.HALF_UP));
        ps.setRealizedPnL(realizedPnL.setScale(2, RoundingMode.HALF_UP));
        ps.setUnrealizedPnL(unrealizedPnL.setScale(2, RoundingMode.HALF_UP));
        log.info("#5 [PortfolioSummaryService:calculatePortfolioSummary] Portfolio Summary: {}", ps);
        
        
        
        
        return ps;
    }

    public void updatePortfolioCash(Long portfolioId) {
        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

        BigDecimal updatedCash = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            BigDecimal totalAmount = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares())).add(tx.getCommissions());
            if (tx.getTransactionType() == TransactionType.BUY) {
                updatedCash = updatedCash.subtract(totalAmount);
            } else if (tx.getTransactionType() == TransactionType.SELL) {
                BigDecimal proceeds = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares())).subtract(tx.getCommissions());
                updatedCash = updatedCash.add(proceeds);
            }
        }

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        portfolio.setCurrentCash(portfolio.getInitialCash().add(updatedCash));
        portfolioRepository.save(portfolio);
    }
    
    
    
    
    
} 
