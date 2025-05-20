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
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.data.PortfolioSummaryDTO;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

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

    	    BigDecimal pnl = txValue.subtract(costBasis).subtract(commissions).setScale(2, RoundingMode.HALF_UP);
    	    log.info("[TransactionService:createTransaction] PnL: {}", pnl);

    	    transaction.setRealizedPnl(pnl);
    	}


    	Transaction savedTransaction = transactionRepository.save(transaction);
    	log.info("[TransactionService:createTransaction] Transaction saved: {}", savedTransaction);
    	
    	// Recalculate holdings for the portfolio using all transactions 
        //holdingsService.recalculateHoldingsForPortfolio(transaction.getPortfolio().getId());
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
}
