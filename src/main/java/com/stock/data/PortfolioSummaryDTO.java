package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioSummaryDTO {
	
	private Long portfolioId;
	private BigDecimal cash;
	private BigDecimal marketValue;
	private BigDecimal totalValue;
	private BigDecimal realizedPnL;
	private BigDecimal unrealizedPnL;
	
	

	public PortfolioSummaryDTO() {
		super();
	}

	public PortfolioSummaryDTO(Long portfolioId,
			BigDecimal cash,
			BigDecimal marketValue,
			BigDecimal totalValue,
			BigDecimal realizedPnL,
			BigDecimal unrealizedPnL) {
		this.portfolioId = portfolioId;
		this.cash = cash;
		this.marketValue = marketValue;
		this.totalValue = totalValue;
		this.realizedPnL = realizedPnL;
		this.unrealizedPnL = unrealizedPnL;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
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

	@Override
	public String toString() {
		return "PortfolioSummaryDTO [portfolioId=" + portfolioId + ", cash=" + cash + ", marketValue=" + marketValue
				+ ", totalValue=" + totalValue + ", realizedPnL=" + realizedPnL + ", unrealizedPnL=" + unrealizedPnL
				+ "]";
	}
	
	
}
