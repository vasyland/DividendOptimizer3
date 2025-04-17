package com.stock.exceptions;

public class DuplicatePortfolioException extends RuntimeException {
    public DuplicatePortfolioException(String message) {
        super(message);
    }
}
