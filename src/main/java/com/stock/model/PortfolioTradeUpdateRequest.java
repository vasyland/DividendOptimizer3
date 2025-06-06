package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioTradeUpdateRequest {
    
    private Long id;  // Transaction ID
    private String symbol;
    private Integer shares;
    private BigDecimal price;
    private BigDecimal commissions;
    private String currency;
    private TransactionType operation;
    private LocalDateTime tradeDate;
    private String note;
    
	public PortfolioTradeUpdateRequest() {

	}

	public PortfolioTradeUpdateRequest(Long id, String symbol, Integer shares, BigDecimal price, BigDecimal commissions,
			String currency, TransactionType operation, LocalDateTime tradeDate, String note) {
		super();
		this.id = id;
		this.symbol = symbol;
		this.shares = shares;
		this.price = price;
		this.commissions = commissions;
		this.currency = currency;
		this.operation = operation;
		this.tradeDate = tradeDate;
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "PortfolioTradeUpdateRequest [id=" + id + ", symbol=" + symbol + ", shares=" + shares + ", price="
				+ price + ", commissions=" + commissions + ", currency=" + currency + ", operation=" + operation
				+ ", tradeDate=" + tradeDate + ", note=" + note + "]";
	}
    
    
}
