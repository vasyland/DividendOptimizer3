package com.stock.model;

public class PortfolioCreateRequest {

//    private Long userId;
    private String name;
    private double initialCash;
    private double currentCash;
    
    
    
	public PortfolioCreateRequest() {
		super();
	}

	public PortfolioCreateRequest(String name, double initialCash, double currentCash) {
		super();
		this.name = name;
		this.initialCash = initialCash;
		this.currentCash = currentCash;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getInitialCash() {
		return initialCash;
	}
	public void setInitialCash(double initialCash) {
		this.initialCash = initialCash;
	}
	public double getCurrentCash() {
		return currentCash;
	}
	public void setCurrentCash(double currentCash) {
		this.currentCash = currentCash;
	}
	@Override
	public String toString() {
		return "PortfolioCreateRequest [name=" + name + ", initialCash=" + initialCash + ", currentCash=" + currentCash
				+ "]";
	}

    
    
    
}
