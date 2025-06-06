package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	@JsonIgnore
	private Portfolio portfolio; // References Portfolio entity

	@Column(nullable = false, length = 10)
	private String symbol;

	@Column(nullable = false)
	private Integer shares;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "commissions")
	private BigDecimal commissions;
	
	@Column(name = "realized_pnl")
	private BigDecimal realizedPnl;

	@Column(name = "currency")
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 4)
	private TransactionType transactionType;

	@Column(name = "transaction_date")
	private LocalDateTime transactionDate;

	@Column(name = "note")
	private String note;

	@Column(name = "created_at", updatable = false)
	@JsonIgnore
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonIgnore
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (transactionDate == null) {
			transactionDate = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
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

	public BigDecimal getRealizedPnl() {
		return realizedPnl;
	}

	public void setRealizedPnl(BigDecimal realizedPnl) {
		this.realizedPnl = realizedPnl;
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
		return "Transaction [id=" + id + ", portfolio=" + portfolio + ", symbol=" + symbol + ", shares=" + shares
				+ ", price=" + price + ", commissions=" + commissions + ", realizedPnl=" + realizedPnl + ", currency="
				+ currency + ", transactionType=" + transactionType + ", transactionDate=" + transactionDate + ", note="
				+ note + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	
}
