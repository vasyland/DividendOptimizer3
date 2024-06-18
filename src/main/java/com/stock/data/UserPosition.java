package com.stock.data;

import java.math.BigDecimal;

public class UserPosition {

  private String symbol;
  private int numberOfShares;
  private BigDecimal averagePrice;

  public UserPosition() {
    super();
  }

  public UserPosition(String symbol, int numberOfShares, BigDecimal averagePrice) {
    super();
    this.symbol = symbol;
    this.numberOfShares = numberOfShares;
    this.averagePrice = averagePrice;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public int getNumberOfShares() {
    return numberOfShares;
  }

  public void setNumberOfShares(int numberOfShares) {
    this.numberOfShares = numberOfShares;
  }

  public BigDecimal getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(BigDecimal averagePrice) {
    this.averagePrice = averagePrice;
  }

  @Override
  public String toString() {
    return "CurrentPosition [symbol=" + symbol + ", numberOfShares=" + numberOfShares
        + ", averagePrice=" + averagePrice + "]";
  }
}
