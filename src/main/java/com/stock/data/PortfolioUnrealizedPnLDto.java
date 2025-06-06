package com.stock.data;

import java.math.BigDecimal;

public class PortfolioUnrealizedPnLDto {
	private Long id;
	private BigDecimal unrealizedPnL;
	private BigDecimal realizedPnL;
	private BigDecimal currentCost;
	private int numberOfHoldings;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public int getNumberOfHoldings() {
		return numberOfHoldings;
	}
	public void setNumberOfHoldings(int numberOfHoldings) {
		this.numberOfHoldings = numberOfHoldings;
	}
	@Override
	public String toString() {
		return "PortfolioUnrealizedPnLDto [id=" + id + ", unrealizedPnL=" + unrealizedPnL + ", realizedPnL="
				+ realizedPnL + ", currentCost=" + currentCost + ", numberOfHoldings=" + numberOfHoldings + "]";
	}
	
	
}