package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "marketing_symbol_status")
public class MarketingStatusSymbol {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="symbol", nullable=false, updatable=true)
	private String symbol;
	@Column(name="current_price")
	private BigDecimal currentPrice;
	@Column(name = "quoterly_dividend_amount")
	private BigDecimal quoterlyDividendAmount;
	@Column(name="current_yield")
	private BigDecimal currentYield;
	@Column(name="allowed_buy_price")
	private BigDecimal allowedBuyPrice;
	@Column(name="best_buy_price")
	private BigDecimal bestBuyPrice;
	@Column(name = "updated_on")
	private LocalDateTime updatedOn;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	public BigDecimal getQuoterlyDividendAmount() {
		return quoterlyDividendAmount;
	}
	public void setQuoterlyDividendAmount(BigDecimal quoterlyDividendAmount) {
		this.quoterlyDividendAmount = quoterlyDividendAmount;
	}
	public BigDecimal getCurrentYield() {
		return currentYield;
	}
	public void setCurrentYield(BigDecimal currentYield) {
		this.currentYield = currentYield;
	}
	public BigDecimal getAllowedBuyPrice() {
		return allowedBuyPrice;
	}
	public void setAllowedBuyPrice(BigDecimal allowedBuyPrice) {
		this.allowedBuyPrice = allowedBuyPrice;
	}
	public BigDecimal getBestBuyPrice() {
		return bestBuyPrice;
	}
	public void setBestBuyPrice(BigDecimal bestBuyPrice) {
		this.bestBuyPrice = bestBuyPrice;
	}
	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	@Override
	public String toString() {
		return "MarketingStatusSymbol [symbol=" + symbol + ", currentPrice=" + currentPrice
				+ ", quoterlyDividendAmount=" + quoterlyDividendAmount + ", currentYield=" + currentYield
				+ ", allowedBuyPrice=" + allowedBuyPrice + ", bestBuyPrice=" + bestBuyPrice + ", updatedOn=" + updatedOn
				+ "]";
	}
}
