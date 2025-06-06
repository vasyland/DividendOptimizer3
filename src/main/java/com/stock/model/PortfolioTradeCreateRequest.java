package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioTradeCreateRequest {

	private Long portfolioId;
	private String symbol;
	private Integer shares;
	private BigDecimal price;
	private TransactionType operation;
	private LocalDateTime tradeDate;
			
	public PortfolioTradeCreateRequest() {
	}
	public PortfolioTradeCreateRequest(Long portfolioId, String symbol, Integer shares, BigDecimal price,
			TransactionType operation, LocalDateTime tradeDate) {
		super();
		this.portfolioId = portfolioId;
		this.symbol = symbol;
		this.shares = shares;
		this.price = price;
		this.operation = operation;
		this.tradeDate = tradeDate;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public TransactionType getOperation() {
		return operation;
	}
	public void setOperation(TransactionType operation) {
		this.operation = operation;
	}
	public LocalDateTime getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(LocalDateTime tradeDate) {
		this.tradeDate = tradeDate;
	}
	@Override
	public String toString() {
		return "PortfolioTradeCreateRequest [portfolioId=" + portfolioId + ", symbol=" + symbol + ", shares=" + shares
				+ ", price=" + price + ", operation=" + operation + ", tradeDate=" + tradeDate + "]";
	}
	
	

}
