package com.stock.services;

/**
 * TransactionService.java
 * 
 * This service class handles the business logic for managing transactions,
 * including creating, editing, deleting, and retrieving transactions.
 * It also includes methods for recalculating holdings and getting current prices.
 * 
 * Holdings should be recalculated after each transaction to ensure	
 * accurate portfolio values.
 */


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.data.PortfolioSummaryDTO;
import com.stock.data.TransactionDto;
import com.stock.exceptions.HoldingNotFoundException;
import com.stock.exceptions.UnauthorizedPortfolioAccessException;
import com.stock.model.CurrentPrice;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.Transaction;
import com.stock.model.TransactionCreateRequest;
import com.stock.model.TransactionType;
import com.stock.repositories.CurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
	
	 private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

	private final TransactionRepository transactionRepository;
	private final PortfolioRepository portfolioRepository;
	private final HoldingRepository holdingRepository;
	private final UserService userService;
	private final HoldingsService holdingsService;
	private final CurrentPriceRepository currentPriceRepository;
//	private final PortfolioSummaryService portfolioSummaryService;
	private final IncrementalPortfolioSummaryService incrementalPortfolioSummaryService;
	private final IncrementalHoldingsService incrementalHoldingsService;
	
	private final IncrementalPortfolioSummaryUpdater incrementalPortfolioSummaryUpdater;
	
	
	
	public TransactionService(TransactionRepository transactionRepository, PortfolioRepository portfolioRepository,
			HoldingRepository holdingRepository, UserService userService, HoldingsService holdingsService,
			CurrentPriceRepository currentPriceRepository,
			IncrementalPortfolioSummaryService incrementalPortfolioSummaryService,
			IncrementalHoldingsService incrementalHoldingsService,
			IncrementalPortfolioSummaryUpdater incrementalPortfolioSummaryUpdater) {
		super();
		this.transactionRepository = transactionRepository;
		this.portfolioRepository = portfolioRepository;
		this.holdingRepository = holdingRepository;
		this.userService = userService;
		this.holdingsService = holdingsService;
		this.currentPriceRepository = currentPriceRepository;
		this.incrementalPortfolioSummaryService = incrementalPortfolioSummaryService;
		this.incrementalHoldingsService = incrementalHoldingsService;
		this.incrementalPortfolioSummaryUpdater = incrementalPortfolioSummaryUpdater;
	}

	/**
	 * 1. Buy transaction goes strait forward into db
	 * 2. Sell transaction needs some calculation on average price of current hoding of the symbol in order tocalculate realized PnL.
	 *   a. We need to take all existing Buy and Sell transactions for this symbol 
	 *   b. to find out what is the average price 
	 */

	/**
     * Save PortfolioTrade (both create and update)
     * @param p
     * @return
     */
    public Transaction createTransaction(TransactionCreateRequest tx) {
    	
    	// Check if a portfolio exists for the current user
    	String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[TransactionService:createTransaction] Current Username: {}", currentUsername);

    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	log.info("[TransactionService:createTransaction] Current User ID: {}", currentUserId);
        
        Optional<Portfolio> portfolioOptional = portfolioRepository.findById(tx.getPortfolioId());
        if (portfolioOptional.isEmpty()) {
        	throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
        }
        
        Portfolio existingPortfolio = portfolioOptional.get();
        Long userId = existingPortfolio.getUser().getId();
        
		if (!userId.equals(currentUserId)) {
			throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
		}
    	
		Transaction transaction = new Transaction();
		transaction.setSymbol(tx.getSymbol());
		transaction.setShares(tx.getShares());
		transaction.setPrice(tx.getPrice());
    	
    	Portfolio temp = new Portfolio();
    	temp.setId(tx.getPortfolioId());
    	
    	transaction.setPortfolio(temp);
    	transaction.setCommissions(tx.getCommissions());
    	transaction.setCurrency(tx.getCurrency());
    	transaction.setTransactionType(tx.getTransactionType());
    	transaction.setTransactionDate(tx.getTransactionDate());
    	transaction.setNote(tx.getNote());
    	
    	// Get holding by portfolio id and by transaction symbol 
    	Optional<Holding> holdingOpt = holdingRepository.findByPortfolioIdAndSymbol(tx.getPortfolioId(), tx.getSymbol());
    	
    	if (tx.getTransactionType() == TransactionType.SELL) {
    		
    		//Get average price from existing transactions for this symbol
    		BigDecimal avgPrice = holdingsService.getPorfolioSymbolAveragePrice(tx.getPortfolioId(), tx.getSymbol());
    		
			log.info("[HoldingsService:getPortfolioHoldings] Average Price for {}: {}", tx.getSymbol(), avgPrice);
			if (avgPrice == null) {
				throw new HoldingNotFoundException("No holding found for symbol: " + tx.getSymbol() + " to create a SELL transaction.");
			}
    		
    		if (holdingOpt.isEmpty()) {
    		    throw new HoldingNotFoundException("No holding found for symbol: " + tx.getSymbol() + " to create a SELL transaction.");
    		}
    		
    	    Holding holding = holdingOpt.get();

    	    BigDecimal txValue = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()));
    	    log.info("[TransactionService:createTransaction] Transaction value: {}", txValue);

    	    BigDecimal avgCost = holding.getAvgCostPerShare();
    	    log.info("[TransactionService:createTransaction] Average cost: {}", avgCost);

    	    BigDecimal costBasis = avgCost.multiply(BigDecimal.valueOf(tx.getShares()));
    	    log.info("[TransactionService:createTransaction] Cost basis: {}", costBasis);

    	    BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;

    	    // Disposition amount of the transaction  
//    	    BigDecimal dispositionAmt = txValue.subtract(costBasis).subtract(commissions).setScale(2, RoundingMode.HALF_UP);
//    	    log.info("[TransactionService:createTransaction] PnL: {}", dispositionAmt);

    	    // (tx.price * tx.shares) - (avgPrice * tx.shares) - commissions
    	    // (tx.price - avgPrice) * tx.shares - commissions
			log.info("[TransactionService:createTransaction] #10 tx.getPrice(): {}, #20 avgPrice: {}, #30 tx.getShares(): {}, #40 commissions: {}",
					tx.getPrice(), avgPrice, tx.getShares(), commissions);
			
    	    BigDecimal realizedPnL = tx.getPrice().subtract(avgPrice).multiply(BigDecimal.valueOf(tx.getShares())).subtract(commissions).setScale(2, RoundingMode.HALF_UP);
			
			log.info("[TransactionService:createTransaction] #4 tx.getPrice(): {}, Realized PnL: {}, #5 avgPrice: {}, Shares: {}", 
					tx.getPrice(), realizedPnL, avgPrice, tx.getShares());
			
			transaction.setRealizedPnL(realizedPnL);
    	    
    	    //transaction.setRealizedPnl(pnl);
    	}


    	Transaction savedTransaction = transactionRepository.save(transaction);
    	log.info("[TransactionService:createTransaction] Transaction saved: {}", savedTransaction);
    	
    	// Recalculate holdings for the portfolio using all transactions 
    	incrementalHoldingsService.applyNewTransaction(savedTransaction);
    	
        // Update portfolio cash after summary calculation
//     	portfolioSummaryService.updatePortfolioCash(tx.getPortfolioId());
    	PortfolioSummaryDTO psDTO = incrementalPortfolioSummaryService.calculatePortfolioSummary(tx.getPortfolioId());
    	log.info("[TransactionService:createTransaction] Portfolio summary - getMarketValue(): {}", psDTO.getMarketValue());
    	log.info("[TransactionService:createTransaction] Portfolio summary - getTotalValue(): {}", psDTO.getTotalValue());
    	log.info("[TransactionService:createTransaction] Portfolio summary - getUnrealizedPnL(): {}", psDTO.getUnrealizedPnL());
     	     	
        // Update portfolio summary after transaction
     	incrementalPortfolioSummaryUpdater.applyTransaction(savedTransaction);
        return savedTransaction;
//        return transactionRepository.save(transaction);
    }


    /**
     * Edit Transaction
     * @param id
     * @param symbol
     * @param shares
     * @param price
     * @param commissions
     * @param currency
     * @param transactionType
     * @param transactionDate
     * @param note
     * @return
     */
    @Transactional
    public Transaction editTransaction(
    		Long id, 
    		String symbol, 
    		Integer shares,
    		BigDecimal price, 
    		BigDecimal commissions,
    		String currency,
    		TransactionType transactionType, 
    		LocalDateTime transactionDate,
    		String note) {
    	
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isEmpty()) {
            throw new IllegalArgumentException("transactionOptional not found.");
        }

        Transaction existingTransaction = transactionOptional.get();
        Long portfolioUserId = existingTransaction.getPortfolio().getUser().getId();
        
        // Check if the portfolio exists for the current user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[TransactionService:editTrade] Current Username: {}", currentUsername);
    	
    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	
    	log.info("[TransactionService:editTransaction] Portfolio userId: {}", portfolioUserId);
    	log.info("[TransactionService:editTransaction] Current User ID: {}", currentUserId);
        
    	if (!currentUserId.equals(portfolioUserId)) {
            throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
        }
    	
        // Check if the portfolio exists for the current user
    	Optional<Portfolio> portfolioOptional = portfolioRepository.findById(existingTransaction.getPortfolio().getId());
		if (portfolioOptional.isEmpty()) {
			throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
		}
        
		existingTransaction.setSymbol(symbol);
		existingTransaction.setShares(shares);
		existingTransaction.setPrice(price);
		existingTransaction.setCommissions(commissions);
		existingTransaction.setCurrency(currency);
		existingTransaction.setTransactionType(transactionType);  // Now passing Enum
		existingTransaction.setTransactionDate(transactionDate != null ? transactionDate : existingTransaction.getTransactionDate());
		existingTransaction.setNote(note);
		
		Transaction saved = transactionRepository.save(existingTransaction);

        holdingsService.recalculateHoldingForSymbol(existingTransaction.getPortfolio().getId(), existingTransaction.getSymbol());
        return saved;
//        return transactionRepository.save(existingTransaction);
    }    
    

	/**
	 * Save Transaction 
	 * @param t
	 * @return
	 */
    public Transaction saveTransaction(Transaction t) {
        return transactionRepository.save(t);
    }   
	

    /**
     * Get Transaction by id
     * @param transactionId
     * @return
     */
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }
	
    
    /**
     * Delete Transaction by id
     * @param id
     */
    @Transactional
    public void deleteTransaction(Long id) {
    	
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found.");
        }

        Transaction existingTransaction = transactionOptional.get();
        Long portfolioUserId = existingTransaction.getPortfolio().getUser().getId();
        
        // Check if the portfolio exists for the current user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[TransactionService:deleteTransaction] Current Username: {}", currentUsername);
    	
    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	
    	log.info("[TransactionService:deleteTransaction] Portfolio userId: {}", portfolioUserId);
    	log.info("[TransactionService:deleteTransaction] Current User ID: {}", currentUserId);
    	
    	if (!currentUserId.equals(portfolioUserId)) {
            throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
        }
    	
        // Check if the portfolio exists for the current user
    	Optional<Portfolio> portfolioOptional = portfolioRepository.findById(existingTransaction.getPortfolio().getId());
		if (portfolioOptional.isEmpty()) {
			throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
		}
		
        Transaction t = transactionOptional.get();
        transactionRepository.delete(t);
        
        holdingsService.recalculateHoldingsForPortfolio(existingTransaction.getPortfolio().getId());
    }
	
	
	// List portfolio transactions
	public List<Transaction> getTransactions(Long portfolioId) {
		return transactionRepository.findByPortfolioId(portfolioId);
	}
	
		
	/**
	 * Get the latest price for a symbol
	 * @param symbol
	 * @return
	 */
	public CurrentPrice getSymbolCurrentPrice(String symbol) {
		CurrentPrice currentPrice = currentPriceRepository.findTopBySymbolOrderByCreatedOnDesc(symbol);
		if (currentPrice == null) {
			throw new IllegalArgumentException("Current price not found");
		}
		return currentPrice;
	}
	
	
	/**
	 * Get the latest prices for a list of symbols
	 * @param symbols
	 * @return
	 */
	public List<CurrentPrice> getLatestPricesForSymbols(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return List.of();
        }
        return currentPriceRepository.findLatestPricesForSymbols(symbols);
    }
	
	
	public List<TransactionDto> getTransactionDtos(Long portfolioId) {
		
		List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);
		if (transactions == null || transactions.isEmpty()) {
			return List.of();
		}
		
		// Group transactions by symbol
        Map<String, List<Transaction>> symbolTransactions = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getSymbol));

        Set<String> symbols = this.extractDistinctSymbols(transactions);
        
        // Get current prices for the symbols
     	List<CurrentPrice> priceList = currentPriceRepository.findBySymbolIn(symbols);
     	
     	List<TransactionDto> transactionDtos = new ArrayList<>();
     	
		for (Transaction t : transactions) {
			
			// Set the current price for each transaction
			CurrentPrice price = priceList.stream()
					.filter(m -> m.getSymbol().equals(t.getSymbol()))
					.findFirst()
					.orElse(null);
			
			if (price != null) {
//				t.setCurrentPrice(price.getPrice());
				TransactionDto tDto = new TransactionDto();
				
				tDto.setId(t.getId());
				tDto.setPortfolio(t.getPortfolio().getId());
				tDto.setSymbol(t.getSymbol());
				tDto.setShares(t.getShares());
				tDto.setPrice(t.getPrice());
				tDto.setCommissions(t.getCommissions());
				
				BigDecimal bookCost = t.getPrice().multiply(BigDecimal.valueOf(t.getShares()))
						.add(t.getCommissions() != null ? t.getCommissions() : BigDecimal.ZERO);
				tDto.setBookCost(bookCost);

				tDto.setCurrency(t.getCurrency());
				tDto.setTransactionType(t.getTransactionType().name());
				tDto.setTransactionDate(t.getTransactionDate());
				tDto.setNote(t.getNote());
				tDto.setCreatedAt(t.getCreatedAt());
				tDto.setUpdatedAt(t.getUpdatedAt());
				tDto.setPnl(t.getRealizedPnL() != null ? t.getRealizedPnL() : BigDecimal.ZERO);
				
				if (t.getTransactionType() == TransactionType.SELL && t.getRealizedPnL() != null) {
					tDto.setPnl(t.getRealizedPnL());
				} else {
					BigDecimal unrealizedPnl = t.getShares() != null && price.getPrice() != null
							? price.getPrice().multiply(BigDecimal.valueOf(t.getShares())).subtract(bookCost)
							: BigDecimal.ZERO;
					tDto.setPnl(unrealizedPnl);
				}
				
				tDto.setPnlPercentage(t.getRealizedPnL() != null && bookCost.compareTo(BigDecimal.ZERO) > 0
						? t.getRealizedPnL().divide(bookCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
						: BigDecimal.ZERO);
				
				transactionDtos.add(tDto);
				
			} else {
				log.warn("[TransactionService:getTransactionDtos] No current price found for symbol: {}",
						t.getSymbol());
			}
		}
		return transactionDtos;
	}
	
	
	/**
	 * Extract distinct symbols from a list of holdings
	 * 
	 * @param holdings
	 * @return
	 */
	public Set<String> extractDistinctSymbols(List<Transaction> holdings) {
		return holdings.stream()
				.map(Transaction::getSymbol)
				.filter(symbol -> symbol != null && !symbol.isBlank())
				.collect(Collectors.toSet()); // Set ensures distinct values
	}
}
