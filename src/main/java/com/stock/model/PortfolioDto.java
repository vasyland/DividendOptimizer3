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
    private BigDecimal initialAmount;
    private BigDecimal currentCash;  
    private BigDecimal totalValue;
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
	public BigDecimal getInitialAmount() {
		return initialAmount;
	}
	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}
	public BigDecimal getCurrentCash() {
		return currentCash;
	}
	public void setCurrentCash(BigDecimal currentCash) {
		this.currentCash = currentCash;
	}
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
