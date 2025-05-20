package com.stock.services;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.data.PortfolioSummaryDTO;
import com.stock.model.CurrentPrice;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioSummary;
import com.stock.repositories.CurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.PortfolioSummaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncrementalPortfolioSummaryService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final CurrentPriceRepository currentPriceRepository;
    private final PortfolioSummaryRepository portfolioSummaryRepository;

    public PortfolioSummaryDTO calculatePortfolioSummary(Long portfolioId) {
    	
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        BigDecimal currentCash = portfolio.getCurrentCash();
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal unrealizedPnL = BigDecimal.ZERO;

        for (Holding h : holdings) {
        	
            List<CurrentPrice> prices = currentPriceRepository.findBySymbol(h.getSymbol());
            
            if (!prices.isEmpty()) {
            	
                BigDecimal marketPrice = prices.get(0).getPrice();
                BigDecimal marketValue = marketPrice.multiply(BigDecimal.valueOf(h.getShares()));
                totalMarketValue = totalMarketValue.add(marketValue);
                unrealizedPnL = unrealizedPnL.add(marketValue.subtract(h.getBookCost()));
            }
        }

        BigDecimal totalValue = currentCash.add(totalMarketValue);

        // Realized PnL comes from the existing portfolio_summary table (updated incrementally elsewhere)
        BigDecimal realizedPnL = portfolioSummaryRepository.findById(portfolioId)
                .map(PortfolioSummary::getRealizedPnL)
                .orElse(BigDecimal.ZERO);

        return new PortfolioSummaryDTO(
                portfolioId,
                currentCash.setScale(2, RoundingMode.HALF_UP),
                totalMarketValue.setScale(2, RoundingMode.HALF_UP),
                totalValue.setScale(2, RoundingMode.HALF_UP),
                realizedPnL.setScale(2, RoundingMode.HALF_UP),
                unrealizedPnL.setScale(2, RoundingMode.HALF_UP)
        );
    }
}
