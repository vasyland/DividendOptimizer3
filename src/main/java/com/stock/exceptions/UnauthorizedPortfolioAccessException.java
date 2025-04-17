package com.stock.exceptions;

public class UnauthorizedPortfolioAccessException extends RuntimeException {
    public UnauthorizedPortfolioAccessException(String message) {
        super(message);
    }
}

