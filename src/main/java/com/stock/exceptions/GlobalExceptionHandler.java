package com.stock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice

public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(UnauthorizedPortfolioAccessException.class)
    public ResponseEntity<String> handleUnauthorizedPortfolioAccess(UnauthorizedPortfolioAccessException ex) {
        log.error("[GlobalExceptionHandler] Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicatePortfolioException.class)
    public ResponseEntity<String> handleDuplicatePortfolio(DuplicatePortfolioException ex) {
        log.error("[GlobalExceptionHandler] Duplicate Portfolio Name: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }    
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("[GlobalExceptionHandler] Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("[GlobalExceptionHandler] Unexpected error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
    
    @ExceptionHandler(HoldingNotFoundException.class)
    public ResponseEntity<String> handleHoldingNotFound(HoldingNotFoundException ex) {
        log.error("[GlobalExceptionHandler] Holding not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}

