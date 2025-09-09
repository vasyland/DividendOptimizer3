package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.stock.data.HoldingDto;
import com.stock.data.HoldingDto2;
import com.stock.model.FmpCurrentPriceProjection;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.FmpCurrentPriceRepository;
import com.stock.repositories.TransactionRepository;

@Service
public class HoldingService2 {

	private static final Logger log = LoggerFactory.getLogger(HoldingService2.class);
	
	private TransactionRepository transactionRepository;
	private FmpCurrentPriceRepository fmpCurrentPriceRepository;
	
	public HoldingService2(TransactionRepository transactionRepository,
			FmpCurrentPriceRepository fmpCurrentPriceRepository) {
		super();
		this.transactionRepository = transactionRepository;
		this.fmpCurrentPriceRepository = fmpCurrentPriceRepository;
	}
	
	
	public List<HoldingDto2> getHoldingsFromTransactions(Long portfolioId) {

		log.info("#1 [HoldingService2:getHoldingsFromTransactions] Portfolio ID = " + portfolioId);

		List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);
		var holdingList = new ArrayList<HoldingDto2>();
		
		// Group transactions by symbol
		Map<String, List<Transaction>> symbolTransactions = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getSymbol));

		var holdingSymbols = new HashSet<String>();
		
		for (Map.Entry<String, List<Transaction>> entry : symbolTransactions.entrySet()) {

			String symbol = entry.getKey();
			List<Transaction> txs = entry.getValue();

			log.info(
					"#2 [HoldingService2:getHoldingsFromTransactions] Transactions for " + symbol + " = " + txs.size());

			int totalShares = 0;
			BigDecimal totalCost = BigDecimal.ZERO;
			String currency = "CAD";

			for (Transaction tx : txs) {

				int shares = tx.getShares();
				BigDecimal price = tx.getPrice();
				BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
				BigDecimal amount = price.multiply(BigDecimal.valueOf(shares)).add(commissions);
				currency = tx.getCurrency();
				
				log.info("#3 " + tx.getTransactionType() + " | " + tx.getSymbol() + " | Shares: " + shares
						+ " | Price: " + price + " | Commissions: " + commissions + " | Amount: " + amount + " | Date: "
						+ tx.getTransactionDate());
				
				if (tx.getTransactionType() == TransactionType.BUY) {

					totalCost = totalCost.add(amount);
					totalShares += shares;

				} else if (tx.getTransactionType() == TransactionType.SELL) {

					if (totalShares == 0)
						continue; // skip if nothing to sell

					BigDecimal soldValue = BigDecimal.valueOf(shares).multiply(price).subtract(commissions);
					BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 10,
							RoundingMode.HALF_UP);
					BigDecimal costReduction = avgCostPerShare.multiply(BigDecimal.valueOf(shares));
					totalCost = totalCost.subtract(costReduction);
					totalShares -= shares;

					log.info("SELL: avgCostPerShare = " + avgCostPerShare + "  soldValue = " + soldValue);
				}
			}

			// Holding Average Cost
			if (totalShares > 0) {
				BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 4, RoundingMode.HALF_UP);
				var holding = new HoldingDto2();
				holding.setSymbol(symbol);
				holding.setPortfolioId(Long.valueOf(portfolioId));
				holding.setShares(totalShares);
				holding.setAvgCostPerShare(avgCostPerShare);
				holding.setBookCost(totalCost.setScale(2, RoundingMode.HALF_UP));
				holding.setNumberOfTransactions(txs.size());
				holding.setCurrency(currency);
				
				holdingList.add(holding);
				holdingSymbols.add(symbol);
			}
		}

		log.info("==================== Processing Holdings =======================");
		// Get current prices for each symbol
	    var currentPrices = fmpCurrentPriceRepository.findBySymbolIn(holdingSymbols);
//		for (FmpCurrentPriceProjection p : currentPrices) {
//			log.info("#4 " + p.getSymbol() + " | " + p.getPrice() + " | " + p.getPriceChange() + " | " + p.getCreatedOn());
//		}
		
		for (HoldingDto2 h : holdingList) {
			
			BigDecimal marketValue = BigDecimal.ZERO;
			BigDecimal unrealizedPnL = BigDecimal.ZERO;
			
			for (FmpCurrentPriceProjection p : currentPrices) {

				if (h.getSymbol().equalsIgnoreCase(p.getSymbol())) {
					
					marketValue = p.getPrice().multiply(BigDecimal.valueOf(h.getShares()));
					unrealizedPnL = p.getPrice().subtract(h.getAvgCostPerShare()).multiply(BigDecimal.valueOf(h.getShares()));
					
					h.setCurrentPrice(p.getPrice().setScale(4, RoundingMode.HALF_UP));
					h.setMarketValue(marketValue.setScale(2, RoundingMode.HALF_UP));
					h.setUnrealizedPnL(unrealizedPnL.setScale(2, RoundingMode.HALF_UP));
					log.info("#5 " +  h.getSymbol() + " Current Price = " + p.getPrice() + " | Market Value = " + marketValue);
					break;
				}
			}
		}
		
		return holdingList;
	}
}
