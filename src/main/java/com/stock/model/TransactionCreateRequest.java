package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCreateRequest {

	private Long portfolioId;
	private String symbol;
	private Integer shares;
	private BigDecimal price;
	private BigDecimal commissions;
	private String currency;
	private TransactionType transactionType;
	private LocalDateTime transactionDate;
	private String note;

	public TransactionCreateRequest() {
	}
		
	public TransactionCreateRequest(Long portfolioId, String symbol, Integer shares, 
			BigDecimal price, BigDecimal commissions, String currency, TransactionType transactionType,
			LocalDateTime transactionDate, String note) {
		super();
		this.portfolioId = portfolioId;
		this.symbol = symbol;
		this.shares = shares;
		
		this.price = price;
		this.commissions = commissions;
		this.currency = currency;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.note = note;
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
	public BigDecimal getCommissions() {
		return commissions;
	}
	public void setCommissions(BigDecimal commissions) {
		this.commissions = commissions;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "TransactionCreateRequest [portfolioId=" + portfolioId + ", symbol=" + symbol + ", shares=" + shares
				+ ", price=" + price + ", commissions=" + commissions + ", currency=" + currency + ", transactionType="
				+ transactionType + ", transactionDate=" + transactionDate + ", note=" + note + "]";
	}

}
