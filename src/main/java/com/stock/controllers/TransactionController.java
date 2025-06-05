package com.stock.controllers;

import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.data.TransactionDto;
import com.stock.model.CurrentPrice;
import com.stock.model.CurrentPriceRequest;
import com.stock.model.Transaction;
import com.stock.model.TransactionCreateRequest;
import com.stock.model.TransactionUpdateRequest;
import com.stock.services.TransactionService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	// Create a new transaction
	@PostMapping("/transaction")
	public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionCreateRequest transactionCreateRequest) {	
		Transaction transaction = transactionService.createTransaction(transactionCreateRequest);
	 	return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	 }
	
	
	/**
	 * Get all transactions for a specific portfolio
	 * @param portfolioId
	 * @return
	 */
//	@GetMapping("/portfolio/{portfolioId}/transactions")
//	public ResponseEntity<List<Transaction>> getTransactions(@PathVariable("portfolioId") Long portfolioId) {
//			List<Transaction> transactions = transactionService.getTransactions(portfolioId);	
//	    	return new ResponseEntity<>(transactions, HttpStatus.OK);
//	 }
	
	
	@GetMapping("/portfolio/{portfolioId}/transactions")
	public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable("portfolioId") Long portfolioId) {
			List<TransactionDto> transactions = transactionService.getTransactionDtos(portfolioId);	
	    	return new ResponseEntity<>(transactions, HttpStatus.OK);
	 }
	
	
	/**
	 * Update a transaction (transactionId in the body)
	 * 
	 * @param p
	 * @return
	 */
	@PutMapping("/transaction")
	public ResponseEntity<?> editTransaction(@RequestBody TransactionUpdateRequest tup,
			BindingResult bindingResult, 
    		HttpServletResponse response) {
		
		log.info("[TransactionController:editTransaction] Transaction to update: {}", tup);
		if (bindingResult.hasErrors()) {
			List<String> errorMessage = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.toList();
			log.error("[TransactionController:editTransaction] Errors in the portfolio transaction:{}", errorMessage);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		// Call service to update existing transaction
		Transaction transaction = transactionService.editTransaction(
				tup.getId(), 
				tup.getSymbol(), 
				tup.getShares(),
				tup.getPrice(), 
				tup.getCommissions(),
				tup.getCurrency(),
				tup.getTransactionType(), 
				tup.getTransactionDate(),
				tup.getNote());

		return new ResponseEntity<>(transaction, HttpStatus.OK);
	}
	
	
	@DeleteMapping("/transaction/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable("id") Long id) {
		transactionService.deleteTransaction(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Get a specific transaction by its ID
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/transaction/{id}")
	public ResponseEntity<Transaction> getTransaction(@PathVariable("id") Long id) {
		Transaction transaction = transactionService.getTransactionById(id);
		return new ResponseEntity<>(transaction, HttpStatus.OK);
	}

	
	@PostMapping("/symbolprice")
	public ResponseEntity<CurrentPrice> getSymbolPrice(@RequestBody CurrentPriceRequest request) {	
		CurrentPrice symbolPrice = transactionService.getSymbolCurrentPrice(request.getSymbol());
	 	return new ResponseEntity<>(symbolPrice, HttpStatus.CREATED);
	}
	
	
	@GetMapping("/portfolio/{portfolioId}/snow-transactions")
	public ResponseEntity<List<Transaction>> getSnowTransactions(@PathVariable("portfolioId") Long portfolioId) {
			List<Transaction> transactions = transactionService.getTransactions(portfolioId);	
	    	return new ResponseEntity<>(transactions, HttpStatus.OK);
	 }
}
