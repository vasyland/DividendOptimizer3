package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.stock.data.HoldingDto;
import com.stock.data.PortfolioSummaryDto2;
import com.stock.model.FmpCurrentPriceProjection;
import com.stock.model.Portfolio;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.FmpCurrentPriceRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.TransactionRepository;

@Service
public class PortfolioService2 {

	private static final Logger log = LoggerFactory.getLogger(PortfolioService2.class);
	
	private PortfolioRepository portfolioRepository;
	private TransactionRepository transactionRepository;
	private FmpCurrentPriceRepository fmpCurrentPriceRepository;
	
	public PortfolioService2(PortfolioRepository portfolioRepository, TransactionRepository transactionRepository,
			FmpCurrentPriceRepository fmpCurrentPriceRepository) {
		super();
		this.portfolioRepository = portfolioRepository;
		this.transactionRepository = transactionRepository;
		this.fmpCurrentPriceRepository = fmpCurrentPriceRepository;
	}

	
	/**
	 * Get all portfolios for a user and calculate their summaries
	 * 
	 * @param userId
	 * @return List of PortfolioSummaryDto2
	 */
	public List<PortfolioSummaryDto2> getUserPortfoliosData(long userId) {

		var userPortfolios = new ArrayList<PortfolioSummaryDto2>();

		// Get all portfolios for the user
		List<Portfolio> portfolioList = portfolioRepository.findByUserId(userId);
		if (portfolioList.isEmpty()) {
			log.warn("[PortfolioService2:getUserPortfoliosData] No portfolios found for user ID: {}", userId);
			return List.of(); // Return an empty list if no portfolios found
		}

		// Loop through each portfolio and calculate its summary
		for (Portfolio p : portfolioList) {
			
			log.info("#666 [PortfolioService2:getUserPortfoliosData] Processing Portfolio ID: " + p.getId() + " | Name: " + p.getName());
			var portfolioSummaryData = this.getPortfolioStatusFromTransactions(p.getId());
			portfolioSummaryData.setName(p.getName());
			portfolioSummaryData.setInitialAmount(p.getInitialAmount());
			userPortfolios.add(portfolioSummaryData);
		}
		return userPortfolios;
	}
	
	
	/**
	 * get records from transactions table and calculate portfolio status
	 * @param portfolioId
	 */
	public PortfolioSummaryDto2 getPortfolioStatusFromTransactions(long portfolioId) {
		
		log.info("#1 [PortfolioService2:getPortfolioStatusFromTransactions] Portfolio ID = " + portfolioId);
		
		var summary = new PortfolioSummaryDto2();
		
		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new RuntimeException("Portfolio not found"));
		
		log.info("#1-1 [PortfolioService2:getPortfolioStatusFromTransactions] Portfolio ID = " + portfolio.getId() + " | Name = " + portfolio.getName());
		
		BigDecimal cash = portfolio.getInitialAmount();
		
		BigDecimal combinedBookCost = BigDecimal.valueOf(0);
		BigDecimal combinedTotal = BigDecimal.valueOf(0);  // BookCost + Cash
		BigDecimal totalMarketValue = BigDecimal.valueOf(0);
		BigDecimal totalUnrealizedPnL = BigDecimal.valueOf(0);
		
		
		List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);
		log.info("#2 [PortfolioService2:getPortfolioStatusFromTransactions] Number of Transactions = " + transactions.size());
		
		// Portfolio doesn't have any transaction
		if(transactions.size() == 0) {
			summary.setId(portfolio.getId());
			summary.setName(portfolio.getName());
			summary.setCombinedBookCost(BigDecimal.valueOf(0));
			summary.setCash(portfolio.getInitialAmount());
			summary.setCombinedTotal(portfolio.getInitialAmount());
			summary.setTotalMarketValue(portfolio.getInitialAmount());
			summary.setTotalUnrealizedPnL(BigDecimal.valueOf(0));
			return summary;
		}
		
		var holdingList = new ArrayList<HoldingDto>();
		
		// Group transactions by symbol
	    Map<String, List<Transaction>> symbolTransactions = transactions.stream()
	        .collect(Collectors.groupingBy(Transaction::getSymbol));
	    
	    for (Map.Entry<String, List<Transaction>> entry : symbolTransactions.entrySet()) {
	    	
		    	String symbol = entry.getKey();
		    	List<Transaction> txs = entry.getValue();
	    	
	    	BigDecimal totalCost = BigDecimal.ZERO;
	        int totalShares = 0;
	        log.info("----------------------------");
	        
	        //Show transactions for each symbol
	        for(Transaction tx : txs) {
	        	
	        	int shares = tx.getShares();
	    		BigDecimal price = tx.getPrice();
	            BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
	            BigDecimal amount = price.multiply(BigDecimal.valueOf(shares)).add(commissions);
				log.info("#2 " + tx.getTransactionType() + " | " + tx.getSymbol() + " | Shares: " + shares + " | Price: "
						+ price + " | Commissions: " + commissions + " | Amount: " + amount + " | Date: "
						+ tx.getTransactionDate());
	           
	            if (tx.getTransactionType() == TransactionType.BUY) {
	    			
	    			totalCost = totalCost.add(amount);
	    			totalShares += shares;
	    			
	    			cash = cash.subtract(amount);
	    			log.info("Cash after buy " + tx.getSymbol() + " = " + cash);
	    			
	    		} else if (tx.getTransactionType() == TransactionType.SELL) {
	
	    			//Register an average price at the moment of sale and that average price should be kept with transaction
	    			
	    			if (totalShares == 0) continue; // skip if nothing to sell
	    			
	    			BigDecimal soldValue = BigDecimal.valueOf(shares).multiply(price).subtract(commissions);
	    			cash = cash.add(soldValue);
	    			log.info("Cash after Sell " + tx.getSymbol() + " = " + cash);
	    			
	    			BigDecimal avgCostPerShare  = totalCost.divide(BigDecimal.valueOf(totalShares),10, RoundingMode.HALF_UP);
	    			BigDecimal costReduction = avgCostPerShare.multiply(BigDecimal.valueOf(shares));
	    			totalCost = totalCost.subtract(costReduction);
	    			totalShares -= shares;
	    			
	    			log.info("SELL: avgCostPerShare = " + avgCostPerShare + "  soldValue = " + soldValue);
	    		}
	        }
	        
	        // Holding Average Cost
	        if (totalShares > 0) {
	        	
	        	BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares),4,RoundingMode.HALF_UP);
		        	
	        	var holding = new HoldingDto();
	        	
	          	 holding.setSymbol(symbol);
	           	 holding.setPortfolioId(Long.valueOf(portfolioId));
	           	 holding.setShares(totalShares);
	           	 holding.setAvgCostPerShare(avgCostPerShare);
	           	 holding.setBookCost(totalCost.setScale(2, RoundingMode.HALF_UP));
	           	 holding.setNumberOfTransactions(txs.size());
	           	 
	           	 log.info("#3 Holding Average Cost = " + avgCostPerShare + " | Shares = " + totalShares + " | BookCost = " + totalCost);
	           	
	           	 holdingList.add(holding);
	        }
	    }
	    
	 // Processing holdings. We need to get a list of symbols from holdings to get current prices based on this list of symbols
	    log.info("==================== Holdings =======================");
	    
	    var holdingSymbols = new HashSet<String>();
	    
	    for(HoldingDto h : holdingList) {
	    	combinedBookCost = combinedBookCost.add(h.getBookCost());
	    	holdingSymbols.add(h.getSymbol());
	    	log.info(h.getSymbol() + " | " + h.getShares() + " | " + h.getAvgCostPerShare() + " | " + h.getBookCost());
	    }
	    
	    log.info("==================== Processing Holdings =======================");
	    // Get current prices for each symbol
	    var currentPrices = fmpCurrentPriceRepository.findBySymbolIn(holdingSymbols);
		for (FmpCurrentPriceProjection p : currentPrices) {
			log.info("#4 " + p.getSymbol() + " | " + p.getPrice() + " | " + p.getPriceChange() + " | " + p.getCreatedOn());
		}
	    
		// Calculate Market Value for each holding
		log.info("==================== Market Value =======================");
		
		for (HoldingDto h : holdingList) {
			
			BigDecimal marketValue = BigDecimal.ZERO;
			BigDecimal unrealizedPnL = BigDecimal.ZERO;
			
            // Find current price for the symbol
			for (FmpCurrentPriceProjection p : currentPrices) {
				if (h.getSymbol().equalsIgnoreCase(p.getSymbol())) {
					marketValue = p.getPrice().multiply(BigDecimal.valueOf(h.getShares()));
					unrealizedPnL = p.getPrice().subtract(h.getAvgCostPerShare()).multiply(BigDecimal.valueOf(h.getShares()));
					
					h.setMarketValue(marketValue.setScale(2, RoundingMode.HALF_UP));
					h.setUnrealizedPnL(unrealizedPnL.setScale(2, RoundingMode.HALF_UP));
					totalMarketValue = totalMarketValue.add(marketValue);
					totalUnrealizedPnL = totalUnrealizedPnL.add(unrealizedPnL);
					
					log.info("#5 " +  h.getSymbol() + " Current Price = " + p.getPrice() + " | Market Value = " + marketValue);
					break;
				}
			}
//			totalMarketValue = totalMarketValue.add(marketValue);
			log.info("#6 " + h.getSymbol() + " | " + h.getShares() + " | " + h.getAvgCostPerShare() + " | "
					+ h.getBookCost() + " | " + h.getMarketValue());
		}
		
		
		combinedTotal = totalMarketValue.add(cash);
		

		log.info("Total Market Value = " + totalMarketValue.setScale(2, RoundingMode.HALF_UP));
		log.info("Combined Total (BookCost + Cash) = " + combinedTotal.setScale(2, RoundingMode.HALF_UP));
		
		summary.setId(Long.valueOf(portfolioId));
		summary.setInitialAmount(portfolio.getInitialAmount());
		summary.setCombinedBookCost(combinedBookCost.setScale(2, RoundingMode.HALF_UP));
		summary.setCombinedTotal(combinedTotal.setScale(2, RoundingMode.HALF_UP));
		summary.setTotalMarketValue(totalMarketValue.setScale(2, RoundingMode.HALF_UP));
		summary.setTotalUnrealizedPnL(totalUnrealizedPnL.setScale(2, RoundingMode.HALF_UP));
		summary.setCash(cash.setScale(2, RoundingMode.HALF_UP));
		summary.setNumberOfHoldings(holdingList.size());
		summary.setNumberOfTransactions(transactions.size());
		summary.setUpdatedAt(LocalDateTime.now());
		
		log.info("[PortfolioService2:getPortfolioStatusFromTransactions] Portfolio Summary:",summary.toString());
		return summary;
	}	
}
