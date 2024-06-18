package com.stock.data;

import java.io.Serializable;
import java.math.BigDecimal;

public class OutputDesicionData implements Serializable {

  public String symbol;
  public int shares;
  public BigDecimal price;
  public BigDecimal symbolAveragePrice;
  public BigDecimal symbolPosition;
  public BigDecimal quaterlyShareDividendAmount;
  public BigDecimal positionDividendAmount;
  public BigDecimal upperYield;
  public BigDecimal midleYield;
  public BigDecimal lowerYield;
  public BigDecimal currentYield;
  public BigDecimal yieldDifference;
  public String action;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public int getShares() {
    return shares;
  }

  public void setShares(int shares) {
    this.shares = shares;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getSymbolAveragePrice() {
    return symbolAveragePrice;
  }

  public void setSymbolAveragePrice(BigDecimal symbolAveragePrice) {
    this.symbolAveragePrice = symbolAveragePrice;
  }

  public BigDecimal getSymbolPosition() {
    return symbolPosition;
  }

  public void setSymbolPosition(BigDecimal symbolPosition) {
    this.symbolPosition = symbolPosition;
  }

  public BigDecimal getQuaterlyShareDividendAmount() {
    return quaterlyShareDividendAmount;
  }

  public void setQuaterlyShareDividendAmount(BigDecimal quaterlyShareDividendAmount) {
    this.quaterlyShareDividendAmount = quaterlyShareDividendAmount;
  }

  public BigDecimal getPositionDividendAmount() {
    return positionDividendAmount;
  }

  public void setPositionDividendAmount(BigDecimal positionDividendAmount) {
    this.positionDividendAmount = positionDividendAmount;
  }

  public BigDecimal getUpperYield() {
    return upperYield;
  }

  public void setUpperYield(BigDecimal upperYield) {
    this.upperYield = upperYield;
  }

  public BigDecimal getMidleYield() {
    return midleYield;
  }

  public void setMidleYield(BigDecimal midleYield) {
    this.midleYield = midleYield;
  }

  public BigDecimal getLowerYield() {
    return lowerYield;
  }

  public void setLowerYield(BigDecimal lowerYield) {
    this.lowerYield = lowerYield;
  }

  public BigDecimal getCurrentYield() {
    return currentYield;
  }

  public void setCurrentYield(BigDecimal currentYield) {
    this.currentYield = currentYield;
  }

  public BigDecimal getYieldDifference() {
    return yieldDifference;
  }

  public void setYieldDifference(BigDecimal yieldDifference) {
    this.yieldDifference = yieldDifference;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  @Override
  public String toString() {
    return "OutputDesicionData [symbol=" + symbol + ", shares=" + shares + ", price=" + price
        + ", symbolAveragePrice=" + symbolAveragePrice + ", symbolPosition=" + symbolPosition
        + ", quaterlyShareDividendAmount=" + quaterlyShareDividendAmount
        + ", positionDividendAmount=" + positionDividendAmount + ", upperYield=" + upperYield
        + ", midleYield=" + midleYield + ", lowerYield=" + lowerYield + ", currentYield="
        + currentYield + ", yieldDifference=" + yieldDifference + ", action=" + action + "]";
  }
}
