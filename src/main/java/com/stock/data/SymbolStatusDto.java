package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SymbolStatusDto {

	private String symbol;  
	private BigDecimal currentPrice;
	private BigDecimal currentYield;
	private BigDecimal lowerYield;
	private BigDecimal upperYield;
	private BigDecimal allowedBuyPrice;
	private BigDecimal allowedBuyYield;
	private BigDecimal bestBuyPrice;
	private BigDecimal quoterlyDividendAmount;
	private BigDecimal sellPointYield;
	private BigDecimal overpricedAmount;
	private BigDecimal overpricedPercentage;
	private BigDecimal sellPrice;
	private LocalDateTime updatedOn;
	private String  recommendedAction;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	public BigDecimal getCurrentYield() {
		return currentYield;
	}
	public void setCurrentYield(BigDecimal currentYield) {
		this.currentYield = currentYield;
	}
	public BigDecimal getLowerYield() {
		return lowerYield;
	}
	public void setLowerYield(BigDecimal lowerYield) {
		this.lowerYield = lowerYield;
	}
	public BigDecimal getUpperYield() {
		return upperYield;
	}
	public void setUpperYield(BigDecimal upperYield) {
		this.upperYield = upperYield;
	}
	public BigDecimal getAllowedBuyPrice() {
		return allowedBuyPrice;
	}
	public void setAllowedBuyPrice(BigDecimal allowedBuyPrice) {
		this.allowedBuyPrice = allowedBuyPrice;
	}
	public BigDecimal getAllowedBuyYield() {
		return allowedBuyYield;
	}
	public void setAllowedBuyYield(BigDecimal allowedBuyYield) {
		this.allowedBuyYield = allowedBuyYield;
	}
	public BigDecimal getBestBuyPrice() {
		return bestBuyPrice;
	}
	public void setBestBuyPrice(BigDecimal bestBuyPrice) {
		this.bestBuyPrice = bestBuyPrice;
	}
	public BigDecimal getQuoterlyDividendAmount() {
		return quoterlyDividendAmount;
	}
	public void setQuoterlyDividendAmount(BigDecimal quoterlyDividendAmount) {
		this.quoterlyDividendAmount = quoterlyDividendAmount;
	}
	public BigDecimal getSellPointYield() {
		return sellPointYield;
	}
	public void setSellPointYield(BigDecimal sellPointYield) {
		this.sellPointYield = sellPointYield;
	}
	public BigDecimal getOverpricedAmount() {
		return overpricedAmount;
	}
	public void setOverpricedAmount(BigDecimal overpricedAmount) {
		this.overpricedAmount = overpricedAmount;
	}
	public BigDecimal getOverpricedPercentage() {
		return overpricedPercentage;
	}
	public void setOverpricedPercentage(BigDecimal overpricedPercentage) {
		this.overpricedPercentage = overpricedPercentage;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	public String getRecommendedAction() {
		return recommendedAction;
	}
	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}
	@Override
	public String toString() {
		return "SymbolStatusDto [symbol=" + symbol + ", currentPrice=" + currentPrice + ", currentYield=" + currentYield
				+ ", lowerYield=" + lowerYield + ", upperYield=" + upperYield + ", allowedBuyPrice=" + allowedBuyPrice
				+ ", allowedBuyYield=" + allowedBuyYield + ", bestBuyPrice=" + bestBuyPrice
				+ ", quoterlyDividendAmount=" + quoterlyDividendAmount + ", sellPointYield=" + sellPointYield
				+ ", overpricedAmount=" + overpricedAmount + ", overpricedPercentage=" + overpricedPercentage
				+ ", sellPrice=" + sellPrice + ", updatedOn=" + updatedOn + ", recommendedAction=" + recommendedAction
				+ "]";
	}
	
	
	
}
