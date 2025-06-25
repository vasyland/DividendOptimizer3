package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.data.HoldingDto;
import com.stock.model.CurrentPrice;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.CurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.TransactionRepository;

@Service
public class HoldingsService {

	private static final Logger log = LoggerFactory.getLogger(HoldingsService.class);
	
    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;
    private CurrentPriceRepository currentPriceRepository;
    private final PortfolioRepository portfolioRepo;

	public HoldingsService(TransactionRepository transactionRepository, HoldingRepository holdingRepository,
			CurrentPriceRepository currentPriceRepository, PortfolioRepository portfolioRepo) {
		super();
		this.transactionRepository = transactionRepository;
		this.holdingRepository = holdingRepository;
		this.currentPriceRepository = currentPriceRepository;
		this.portfolioRepo = portfolioRepo;
	}


	/**
	 * This method calculates the average price of the holding of one symbol.
	 * We need average price in order to calculate realized PnL for a Sell transaction.
	 * @param portfolioId
	 * @param symbol
	 * @return Average price for a holding in portfolio
	 */
	public BigDecimal getPorfolioSymbolAveragePrice(Long portfolioId, String symbol) {

		// Get all transactions for portfolio for one symbol
		List<Transaction> transactions = transactionRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);

		BigDecimal totalCost = BigDecimal.ZERO;
		int totalShares = 0;
		var avgCostBasis = new HashMap<String, BigDecimal>();

		for (Transaction tx : transactions) {
			int shares = tx.getShares();
			BigDecimal price = tx.getPrice();
			BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
			BigDecimal transactionBookCost = price.multiply(BigDecimal.valueOf(shares)).add(commissions);
			
			if (tx.getTransactionType() == TransactionType.BUY) {
				totalCost = totalCost.add(transactionBookCost);
				totalShares += shares;

			} else if (tx.getTransactionType() == TransactionType.SELL) {
				totalCost = totalCost.subtract(transactionBookCost);
				totalShares -= shares;
			}
		}
		BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 10, RoundingMode.HALF_UP);
		return avgCostPerShare;
	}
	
	/**
     * Recalculate and update the holding for a specific symbol in a portfolio
     */
    @Transactional
    public void recalculateHoldingForSymbol(Long portfolioId, String symbol) {
        List<Transaction> transactions = transactionRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);

        if (transactions.isEmpty()) {
            holdingRepository.deleteByPortfolioIdAndSymbol(portfolioId, symbol);
            return;
        }

        int totalShares = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal realizedPnL = BigDecimal.ZERO;
        String currency = transactions.get(0).getCurrency();

        for (Transaction tx : transactions) {
            int shares = tx.getShares();
            BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
            BigDecimal tradeValue = tx.getPrice().multiply(BigDecimal.valueOf(shares));

            if (tx.getTransactionType() == TransactionType.BUY) {
                totalShares += shares;
                totalCost = totalCost.add(tradeValue).add(commissions); // ✅ add commission to cost
            } else if (tx.getTransactionType() == TransactionType.SELL) {
                totalShares -= shares;
                // ❌ DO NOT modify totalCost!
                // Optionally track realized profit/loss separately
            }
        }

        if (totalShares <= 0) {
            holdingRepository.deleteByPortfolioIdAndSymbol(portfolioId, symbol);
            return;
        }

        BigDecimal avgCost = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
        BigDecimal bookCost = totalCost.setScale(2, RoundingMode.HALF_UP);

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, symbol)
            .orElseGet(() -> {
                Holding h = new Holding();
                h.setPortfolio(transactions.get(0).getPortfolio());
                h.setSymbol(symbol);
                return h;
            });

        holding.setShares(totalShares);
        holding.setAvgCostPerShare(avgCost);
        holding.setBookCost(bookCost);
        holding.setCurrency(currency);

        holdingRepository.save(holding);
    }


    /**
     * Recalculate holdings for all symbols in a given portfolio.
     */
    @Transactional
    public void recalculateHoldingsForPortfolio(Long portfolioId) {
        holdingRepository.deleteByPortfolioId(portfolioId);

        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

        // Group transactions by symbol
        Map<String, List<Transaction>> symbolTransactions = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getSymbol));

        for (Map.Entry<String, List<Transaction>> entry : symbolTransactions.entrySet()) {
        	
            String symbol = entry.getKey();
            List<Transaction> txs = entry.getValue();
            // Sort transactions by createdAt
            txs.sort(Comparator.comparing(Transaction::getCreatedAt));
            
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal realizedPnL = BigDecimal.ZERO;
            
            int totalShares = 0;

            for (Transaction tx : txs) {
            	
//            	log.info("[HoldingsService:recalculateHoldingsForPortfolio] #1 Processing transaction: {}", tx);
            	
                int shares = tx.getShares();
                BigDecimal price = tx.getPrice();
                BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
                BigDecimal amount = price.multiply(BigDecimal.valueOf(shares)).add(commissions);

                log.info("[HoldingsService:recalculateHoldingsForPortfolio] #2 Transaction amount: {}, shares: {}", amount, shares);
                
                if (tx.getTransactionType() == TransactionType.BUY) {
                	
                    totalCost = totalCost.add(amount);
                    totalShares += shares;
                    
                } else if (tx.getTransactionType() == TransactionType.SELL) {
                	
                    if (totalShares == 0) continue; // skip if nothing to sell

                    BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 10, RoundingMode.HALF_UP);
                    BigDecimal costReduction = avgCostPerShare.multiply(BigDecimal.valueOf(shares));
                    
                    totalCost = totalCost.subtract(costReduction);
                    totalShares -= shares;
                    realizedPnL = realizedPnL.add(tx.getRealizedPnL() != null ? tx.getRealizedPnL() : BigDecimal.ZERO);
                    
                    log.info("[HoldingsService:recalculateHoldingsForPortfolio] #3 Updated realized PnL: {}", realizedPnL);
                }
            }

            if (totalShares > 0) {
                BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
//                log.info("[HoldingsService:recalculateHoldingsForPortfolio] #4 Avg Cost Per Share for {}: {}", symbol, avgCostPerShare);
                
                Holding holding = new Holding();
                holding.setPortfolio(txs.get(0).getPortfolio());
                holding.setSymbol(symbol);
                holding.setShares(totalShares);
                holding.setAvgCostPerShare(avgCostPerShare);
                holding.setBookCost(totalCost.setScale(2, RoundingMode.HALF_UP));
                holding.setCurrency(txs.get(0).getCurrency());
                holding.setRealizedPnL(realizedPnL.setScale(2, RoundingMode.HALF_UP));

                holdingRepository.save(holding);
            }
        }
    }

    
    
    /**
     * Calculating current holdings. Those transactions that are sold are not included. 
     * @param portfolioId
     * @return
     */
    public List<HoldingDto> getPortfolioHoldings(long portfolioId, boolean isTableUpdate) {
    	
    	if (isTableUpdate) {
    		holdingRepository.deleteByPortfolioId(portfolioId);
    	}
    	
    	
        Portfolio portfolio = portfolioRepo.findById(portfolioId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

        Set<String> symbols = this.extractDistinctSymbols(transactions);
        List<CurrentPrice> priceList = currentPriceRepository.findBySymbolIn(symbols);

        Map<String, List<Transaction>> symbolTransactions = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getSymbol));

        List<HoldingDto> holdings = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : symbolTransactions.entrySet()) {
            String symbol = entry.getKey();
            List<Transaction> txs = entry.getValue();

            BigDecimal totalCost = BigDecimal.ZERO;
            int totalShares = 0;
            BigDecimal realizedPnL = BigDecimal.ZERO;

            for (Transaction tx : txs) {
                int shares = tx.getShares();
                BigDecimal price = tx.getPrice();
                BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
                BigDecimal transactionCost = price.multiply(BigDecimal.valueOf(shares)).add(commissions);

                if (tx.getTransactionType() == TransactionType.BUY) {
                    totalCost = totalCost.add(transactionCost);
                    totalShares += shares;
                } else if (tx.getTransactionType() == TransactionType.SELL) {
                    if (totalShares == 0) continue;

                    BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 10, RoundingMode.HALF_UP);
                    BigDecimal costBasis = avgCostPerShare.multiply(BigDecimal.valueOf(shares));
                    totalCost = totalCost.subtract(costBasis);
                    totalShares -= shares;

                    BigDecimal pnl = price.subtract(avgCostPerShare)
                            .multiply(BigDecimal.valueOf(shares))
                            .subtract(commissions);

                    realizedPnL = realizedPnL.add(pnl);
                }
            }

            // Only include symbols with remaining shares
            if (totalShares > 0) {
                BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
                BigDecimal bookCost = totalCost.setScale(2, RoundingMode.HALF_UP);

                BigDecimal currentPrice = priceList.stream()
                        .filter(cp -> cp.getSymbol().equals(symbol))
                        .map(CurrentPrice::getPrice)
                        .findFirst()
                        .orElse(BigDecimal.ZERO);

                BigDecimal marketValue = currentPrice.multiply(BigDecimal.valueOf(totalShares));
                BigDecimal unrealizedPnL = marketValue.subtract(bookCost);

                HoldingDto dto = new HoldingDto();
                dto.setPortfolioId(portfolioId);
                dto.setSymbol(symbol);
                dto.setShares(totalShares);
                dto.setAvgCostPerShare(avgCostPerShare);
                dto.setBookCost(bookCost);
                dto.setUnrealizedPnL(unrealizedPnL.setScale(2, RoundingMode.HALF_UP));
                dto.setMarketValue(marketValue.setScale(2, RoundingMode.HALF_UP));
                dto.setCurrency(txs.get(0).getCurrency());
                dto.setRealizedPnL(realizedPnL.setScale(2, RoundingMode.HALF_UP));
                holdings.add(dto);
                
				if (isTableUpdate) {
					
					Holding holding = new Holding();
					holding.setPortfolio(txs.get(0).getPortfolio());
					holding.setSymbol(symbol);
					holding.setShares(totalShares);
					holding.setAvgCostPerShare(avgCostPerShare);
					holding.setBookCost(bookCost);
					holding.setCurrency(txs.get(0).getCurrency());
					holding.setRealizedPnL(realizedPnL.setScale(2, RoundingMode.HALF_UP));
					// Save the holding to the repository
					holdingRepository.save(holding);
				}
            }
        }
        return holdings;
    }
	
	
    /**
	 * Extract distinct symbols from a list of holdings
	 * 
	 * @param holdings
	 * @return
	 */
	public Set<String> extractDistinctSymbols(List<Transaction> transactions) {
		return transactions.stream()
				.map(Transaction::getSymbol)
				.filter(symbol -> symbol != null && !symbol.isBlank())
				.collect(Collectors.toSet()); // Set ensures distinct values
	}
}


