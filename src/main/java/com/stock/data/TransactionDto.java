package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

	private Long id;
	private Long portfolio;
	private String symbol;
	private Integer shares;
	private BigDecimal price;
	private BigDecimal commissions;
	private BigDecimal bookCost;
	private BigDecimal unrealizedPnL;
	private BigDecimal PnlPercentage;
	private String currency;
	private String transactionType;
	private LocalDateTime transactionDate;
	private String note;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public TransactionDto() {

	}

	public TransactionDto(Long id, Long portfolio, String symbol, Integer shares, BigDecimal price,
			BigDecimal commissions, BigDecimal bookCost, BigDecimal pnl, BigDecimal pnlPercentage, String currency,
			String transactionType, LocalDateTime transactionDate, String note, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.portfolio = portfolio;
		this.symbol = symbol;
		this.shares = shares;
		this.price = price;
		this.commissions = commissions;
		this.bookCost = bookCost;
		unrealizedPnL = pnl;
		PnlPercentage = pnlPercentage;
		this.currency = currency;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.note = note;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Long portfolio) {
		this.portfolio = portfolio;
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

	public BigDecimal getBookCost() {
		return bookCost;
	}

	public void setBookCost(BigDecimal bookCost) {
		this.bookCost = bookCost;
	}

	public BigDecimal getPnl() {
		return unrealizedPnL;
	}

	public void setPnl(BigDecimal pnl) {
		unrealizedPnL = pnl;
	}

	public BigDecimal getPnlPercentage() {
		return PnlPercentage;
	}

	public void setPnlPercentage(BigDecimal pnlPercentage) {
		PnlPercentage = pnlPercentage;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "TransactionDto [id=" + id + ", portfolio=" + portfolio + ", symbol=" + symbol + ", shares=" + shares
				+ ", price=" + price + ", commissions=" + commissions + ", bookCost=" + bookCost + ", Pnl=" + unrealizedPnL
				+ ", PnlPercentage=" + PnlPercentage + ", currency=" + currency + ", transactionType=" + transactionType
				+ ", transactionDate=" + transactionDate + ", note=" + note + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}

}
