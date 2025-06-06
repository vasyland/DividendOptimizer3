package com.stock.model;

import jakarta.persistence.Entity;

public class CurrentPriceRequest {
	String symbol;
	
	public CurrentPriceRequest() {
	}

	public CurrentPriceRequest(String symbol) {
		super();
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "CurrentPriceRequest [symbol=" + symbol + "]";
	}
	
	
}
