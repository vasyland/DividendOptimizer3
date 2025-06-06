package com.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "listed_companies", indexes = {
    @Index(name = "idx_listed_name", columnList = "name")
})
public class ListedCompany {

    @Id
    @Column(length = 11)
    private String symbol;  // PRIMARY KEY

    @Column(length = 100)
    private String name;

    @Column
    private Long marketcap;

    @Column(length = 6)
    private String exchange;

	public ListedCompany() {
		super();
	}

	public ListedCompany(String symbol, String name, Long marketcap, String exchange) {
		super();
		this.symbol = symbol;
		this.name = name;
		this.marketcap = marketcap;
		this.exchange = exchange;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getMarketcap() {
		return marketcap;
	}

	public void setMarketcap(Long marketcap) {
		this.marketcap = marketcap;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	@Override
	public String toString() {
		return "ListedCompany [symbol=" + symbol + ", name=" + name + ", marketcap=" + marketcap + ", exchange="
				+ exchange + "]";
	}
    
    
}
