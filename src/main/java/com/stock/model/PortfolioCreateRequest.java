package com.stock.model;

import java.math.BigDecimal;

public class PortfolioCreateRequest {

    private String name;
    private BigDecimal initialAmount;

    // Default constructor for Jackson
    public PortfolioCreateRequest() {
    }

    // Your custom constructor
    public PortfolioCreateRequest(String name, BigDecimal initialAmount) {
        this.name = name;
        this.initialAmount = initialAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }
}