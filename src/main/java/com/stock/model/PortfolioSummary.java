package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_summary")
public class PortfolioSummary {

    @Id
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(name = "total_market_value")
    private BigDecimal totalMarketValue;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @Column(name = "realized_pnl")
    private BigDecimal realizedPnL;

    @Column(name = "unrealized_pnl")
    private BigDecimal unrealizedPnL;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public BigDecimal getTotalMarketValue() {
		return totalMarketValue;
	}

	public void setTotalMarketValue(BigDecimal totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getRealizedPnL() {
		return realizedPnL;
	}

	public void setRealizedPnL(BigDecimal realizedPnL) {
		this.realizedPnL = realizedPnL;
	}

	public BigDecimal getUnrealizedPnL() {
		return unrealizedPnL;
	}

	public void setUnrealizedPnL(BigDecimal unrealizedPnL) {
		this.unrealizedPnL = unrealizedPnL;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "PortfolioSummary [portfolioId=" + portfolioId + ", portfolio=" + portfolio + ", totalMarketValue="
				+ totalMarketValue + ", cash=" + cash + ", totalValue=" + totalValue + ", realizedPnL=" + realizedPnL
				+ ", unrealizedPnL=" + unrealizedPnL + ", updatedAt=" + updatedAt + "]";
	}
    
    
}
