package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * This class is to bring all information about Portfolio upfront.
 */
public class PortfolioDto {
    
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal initialCash;
    private BigDecimal currentCash;  
    private BigDecimal currentCost;
    private BigDecimal realizedPnL;
    private BigDecimal unrealizedPnL;
    private BigDecimal totalValue;
    private BigDecimal pnl;
    private BigDecimal pnlPercent;
    private int numberOfholdings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getInitialCash() {
		return initialCash;
	}
	public void setInitialCash(BigDecimal initialCash) {
		this.initialCash = initialCash;
	}
	public BigDecimal getCurrentCash() {
		return currentCash;
	}
	public void setCurrentCash(BigDecimal currentCash) {
		this.currentCash = currentCash;
	}
	public BigDecimal getCurrentCost() {
		return currentCost;
	}
	public void setCurrentCost(BigDecimal currentCost) {
		this.currentCost = currentCost;
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
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public BigDecimal getPnl() {
		return pnl;
	}
	public void setPnl(BigDecimal pnl) {
		this.pnl = pnl;
	}
	public BigDecimal getPnlPercent() {
		return pnlPercent;
	}
	public void setPnlPercent(BigDecimal pnlPercent) {
		this.pnlPercent = pnlPercent;
	}
	public int getNumberOfholdings() {
		return numberOfholdings;
	}
	public void setNumberOfholdings(int numberOfholdings) {
		this.numberOfholdings = numberOfholdings;
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
		return "PortfolioDto [id=" + id + ", userId=" + userId + ", name=" + name + ", initialCash=" + initialCash
				+ ", currentCash=" + currentCash + ", currentCost=" + currentCost + ", realizedPnL=" + realizedPnL
				+ ", unrealizedPnL=" + unrealizedPnL + ", totalValue=" + totalValue + ", pnl=" + pnl + ", pnlPercent="
				+ pnlPercent + ", numberOfholdings=" + numberOfholdings + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}    
    
    
}
