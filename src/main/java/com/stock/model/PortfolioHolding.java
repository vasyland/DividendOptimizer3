package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class PortfolioHolding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	@JsonIgnore
	private Portfolio portfolio; // References Portfolio entity
	
	@Column(nullable = false, length = 10)
	private String symbol;
	
	@Column(nullable = false)
	private Integer shares;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal averagePrice;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal bookCost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Integer getShares() {
		return shares;
	}

	public void setShares(Integer shares) {
		this.shares = shares;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public BigDecimal getBookCost() {
		return bookCost;
	}

	public void setBookCost(BigDecimal bookCost) {
		this.bookCost = bookCost;
	}

	@Override
	public String toString() {
		return "PortfolioHolding [id=" + id + ", portfolio=" + portfolio + ", symbol=" + symbol + ", shares=" + shares
				+ ", averagePrice=" + averagePrice + ", bookCost=" + bookCost + "]";
	}
    
	
}
