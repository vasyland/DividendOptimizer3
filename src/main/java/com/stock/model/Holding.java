package com.stock.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holding", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"portfolio_id", "symbol"})
})
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false)
    private Integer shares;

    @Column(name = "avg_cost_per_share", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgCostPerShare;

    @Column(name = "book_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal bookCost;
    
    @Column(name = "realized_pnl")
    private BigDecimal realizedPnL;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

	public BigDecimal getRealizedPnL() {
		return realizedPnL;
	}

	public void setRealizedPnL(BigDecimal realizedPnL) {
		this.realizedPnL = realizedPnL;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
		return "Holding [id=" + id + ", portfolio=" + portfolio + ", symbol=" + symbol + ", shares=" + shares
				+ ", avgCostPerShare=" + avgCostPerShare + ", bookCost=" + bookCost + ", realizedPnL=" + realizedPnL
				+ ", currency=" + currency + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
    
    
}
