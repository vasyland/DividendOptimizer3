package com.stock.data;

import java.math.BigDecimal;

public class HoldingDto2 {

	private Long portfolioId;
	private String symbol;
	private int shares;
	private BigDecimal avgCostPerShare;
	private BigDecimal currentPrice;
	private BigDecimal bookCost;
	private BigDecimal marketValue;
	private BigDecimal unrealizedPnL;
	private int numberOfTransactions;
	private String currency;
	
	public HoldingDto2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HoldingDto2(Long portfolioId, String symbol, int shares, BigDecimal avgCostPerShare, BigDecimal currentPrice,
			BigDecimal bookCost, BigDecimal marketValue, BigDecimal unrealizedPnL, int numberOfTransactions,
			String currency) {
		super();
		this.portfolioId = portfolioId;
		this.symbol = symbol;
		this.shares = shares;
		this.avgCostPerShare = avgCostPerShare;
		this.currentPrice = currentPrice;
		this.bookCost = bookCost;
		this.marketValue = marketValue;
		this.unrealizedPnL = unrealizedPnL;
		this.numberOfTransactions = numberOfTransactions;
		this.currency = currency;
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

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

	public BigDecimal getAvgCostPerShare() {
		return avgCostPerShare;
	}

	public void setAvgCostPerShare(BigDecimal avgCostPerShare) {
		this.avgCostPerShare = avgCostPerShare;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public BigDecimal getBookCost() {
		return bookCost;
	}

	public void setBookCost(BigDecimal bookCost) {
		this.bookCost = bookCost;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getUnrealizedPnL() {
		return unrealizedPnL;
	}

	public void setUnrealizedPnL(BigDecimal unrealizedPnL) {
		this.unrealizedPnL = unrealizedPnL;
	}

	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
