package com.stock.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Symbol {
	
	@Id
	@Column(name="Symbol", nullable=false, updatable=false)
	private String symbol;

	public Symbol(String symbol) {
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
		return "Symbol [symbol=" + symbol + "]";
	}
	
	
}
