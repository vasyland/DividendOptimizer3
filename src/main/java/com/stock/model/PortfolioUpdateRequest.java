package com.stock.model;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortfolioUpdateRequest {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("initialAmount")
	private BigDecimal initialAmount;
	
	public PortfolioUpdateRequest() {
		super();
	}

	public PortfolioUpdateRequest(Long id, String name, BigDecimal initialAmount) {
		super();
		this.id = id;
		this.name = name;
		this.initialAmount = initialAmount;
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

	public BigDecimal getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}
}
