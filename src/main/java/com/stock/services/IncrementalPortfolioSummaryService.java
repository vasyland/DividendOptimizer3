package com.stock.services;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.data.PortfolioSummaryDTO;
import com.stock.model.FmpCurrentPriceProjection;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioSummary;
import com.stock.repositories.FmpCurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.PortfolioSummaryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncrementalPortfolioSummaryService {
	
	private static final Logger log = LoggerFactory.getLogger(IncrementalPortfolioSummaryService.class);

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final FmpCurrentPriceRepository fmpCurrentPriceRepository;
    private final PortfolioSummaryRepository portfolioSummaryRepository;
 
	public IncrementalPortfolioSummaryService(PortfolioRepository portfolioRepository,
			HoldingRepository holdingRepository,
			FmpCurrentPriceRepository fmpCurrentPriceRepository,
			PortfolioSummaryRepository portfolioSummaryRepository) {
		super();
		this.portfolioRepository = portfolioRepository;
		this.holdingRepository = holdingRepository;
		this.fmpCurrentPriceRepository = fmpCurrentPriceRepository;
		this.portfolioSummaryRepository = portfolioSummaryRepository;
	}


	/**
	 * 
	 * @param portfolioId
	 * @return
	 */
    public PortfolioSummaryDTO calculatePortfolioSummary(Long portfolioId) {
    	
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        BigDecimal currentCash = portfolio.getCurrentCash();
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal unrealizedPnL = BigDecimal.ZERO;

        for (Holding h : holdings) {

            FmpCurrentPriceProjection currentPriceData = fmpCurrentPriceRepository.findBySymbol(h.getSymbol()).get();
            
            if (currentPriceData != null) {
            	
                BigDecimal marketPrice = currentPriceData.getPrice();
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
