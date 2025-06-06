package com.stock.model;


import java.math.BigDecimal;

public class PortfolioUpdateRequest {

	private Long id;
	private String name;
	private BigDecimal initialCash;
	private BigDecimal currentCash;
	
	public PortfolioUpdateRequest() {
		super();
	}

	public PortfolioUpdateRequest(Long id, String name, BigDecimal initialCash, BigDecimal currentCash) {
		super();
		this.id = id;
		this.name = name;
		this.initialCash = initialCash;
		this.currentCash = currentCash;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "PortfolioUpdateRequest [id=" + id + ", name=" + name + ", initialCash=" + initialCash + ", currentCash="
				+ currentCash + "]";
	}
	
	
	
}
