package com.stock.data;

import java.math.BigDecimal;

public class HoldingPnLDto {
	
	private Long id;
	private Long portfolioId;
	private String symbol;
	private Integer shares;
	private BigDecimal avgCostPerShare;
	private BigDecimal bookCost;
	private BigDecimal unrealizedPnL;
	private BigDecimal realizedPnL;
	private BigDecimal currentCost;
	private String currency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Integer getShares() {
		return shares;
	}

	public void setShares(Integer shares) {
		this.shares = shares;
	}

	public BigDecimal getAvgCostPerShare() {
		return avgCostPerShare;
	}

	public void setAvgCostPerShare(BigDecimal avgCostPerShare) {
		this.avgCostPerShare = avgCostPerShare;
	}

	public BigDecimal getBookCost() {
		return bookCost;
	}

	public void setBookCost(BigDecimal bookCost) {
		this.bookCost = bookCost;
	}

	public BigDecimal getUnrealizedPnL() {
		return unrealizedPnL;
	}

	public void setUnrealizedPnL(BigDecimal unrealizedPnL) {
		this.unrealizedPnL = unrealizedPnL;
	}

	public BigDecimal getRealizedPnL() {
		return realizedPnL;
	}

	public void setRealizedPnL(BigDecimal realizedPnL) {
		this.realizedPnL = realizedPnL;
	}

	public BigDecimal getCurrentCost() {
		return currentCost;
	}

	public void setCurrentCost(BigDecimal currentCost) {
		this.currentCost = currentCost;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "HoldingPnLDto [id=" + id + ", portfolioId=" + portfolioId + ", symbol=" + symbol + ", shares=" + shares
				+ ", avgCostPerShare=" + avgCostPerShare + ", bookCost=" + bookCost + ", unrealizedPnL=" + unrealizedPnL
				+ ", realizedPnL=" + realizedPnL + ", currentCost=" + currentCost + ", currency=" + currency + "]";
	}

}