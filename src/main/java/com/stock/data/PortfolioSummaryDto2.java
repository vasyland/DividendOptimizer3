package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioSummaryDto2 {

    private Long id;
    private String name;
    private BigDecimal initialAmount;
    private BigDecimal cash;
    private BigDecimal combinedBookCost;
    private BigDecimal combinedTotal;
    private BigDecimal totalMarketValue;
    private BigDecimal totalUnrealizedPnL;
    private int numberOfTransactions;
    private int numberOfHoldings;
    private LocalDateTime updatedAt;
    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getInitialAmount() {
		return initialAmount;
	}
	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}
	public BigDecimal getCash() {
		return cash;
	}
	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}
	public BigDecimal getCombinedBookCost() {
		return combinedBookCost;
	}
	public void setCombinedBookCost(BigDecimal combinedBookCost) {
		this.combinedBookCost = combinedBookCost;
	}
	public BigDecimal getCombinedTotal() {
		return combinedTotal;
	}
	public void setCombinedTotal(BigDecimal combinedTotal) {
		this.combinedTotal = combinedTotal;
	}
	public BigDecimal getTotalMarketValue() {
		return totalMarketValue;
	}
	public void setTotalMarketValue(BigDecimal totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getNumberOfHoldings() {
		return numberOfHoldings;
	}
	public void setNumberOfHoldings(int numberOfHoldings) {
		this.numberOfHoldings = numberOfHoldings;
	}
	
	
	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}
	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}
	public BigDecimal getTotalUnrealizedPnL() {
		return totalUnrealizedPnL;
	}
	public void setTotalUnrealizedPnL(BigDecimal totalUnrealizedPnL) {
		this.totalUnrealizedPnL = totalUnrealizedPnL;
	}
	
	
	
}
