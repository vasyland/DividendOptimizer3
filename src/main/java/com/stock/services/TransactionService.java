package com.stock.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.exceptions.UnauthorizedPortfolioAccessException;
import com.stock.model.CurrentPrice;
import com.stock.model.Portfolio;
import com.stock.model.Transaction;
import com.stock.model.TransactionCreateRequest;
import com.stock.model.TransactionType;
import com.stock.repositories.CurrentPriceRepository;
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
	private final UserService userService;
	private final HoldingsService holdingsService;
	private final CurrentPriceRepository currentPriceRepository;
	
	/**
     * Save PortfolioTrade (both create and update)
     * @param p
     * @return
     */
    public Transaction createTransaction(TransactionCreateRequest p) {
    	
    	// Check if the portfolio exists for the current user
    	String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[TransactionService:createTransaction] Current Username: {}", currentUsername);

    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	log.info("[TransactionService:createTransaction] Current User ID: {}", currentUserId);
        
        Optional<Portfolio> portfolioOptional = portfolioRepository.findById(p.getPortfolioId());
        if (portfolioOptional.isEmpty()) {
        	throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
        }
        
        Portfolio existingPortfolio = portfolioOptional.get();
        Long userId = existingPortfolio.getUser().getId();
        
		if (!userId.equals(currentUserId)) {
			throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
		}
    	
		Transaction transaction = new Transaction();
		transaction.setSymbol(p.getSymbol());
		transaction.setShares(p.getShares());
		transaction.setPrice(p.getPrice());
    	
    	Portfolio temp = new Portfolio();
    	temp.setId(p.getPortfolioId());
    	
    	transaction.setPortfolio(temp);
    	transaction.setCommissions(p.getCommissions());
    	transaction.setCurrency(p.getCurrency());
    	transaction.setTransactionType(p.getTransactionType());
    	transaction.setTransactionDate(p.getTransactionDate());
    	transaction.setNote(p.getNote());
    	
    	Transaction savedTransaction = transactionRepository.save(transaction);
//        holdingsService.recalculateHoldingForSymbol(transaction.getPortfolio().getId(), transaction.getSymbol());
        
        holdingsService.recalculateHoldingsForPortfolio(transaction.getPortfolio().getId());
        
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
	
	
	public CurrentPrice getSymbolCurrentPrice(String symbol) {
		CurrentPrice currentPrice = currentPriceRepository.findTopBySymbolOrderByCreatedOnDesc(symbol);
		if (currentPrice == null) {
			throw new IllegalArgumentException("Current price not found");
		}
		return currentPrice;
	}
}
