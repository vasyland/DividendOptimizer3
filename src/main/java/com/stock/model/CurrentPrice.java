package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "current_price", indexes = {
		@Index(name = "idx_price_symbol", columnList = "symbol")
})
public class CurrentPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="symbol", nullable = false, length = 11)
	private String symbol;

	@Column(name="price")
	private BigDecimal price;
	
	@Column(name="price_change")
	private BigDecimal price_change;
	
	@Column(name = "created_on")
	private LocalDateTime createdOn;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice_change() {
		return price_change;
	}

	public void setPrice_change(BigDecimal price_change) {
		this.price_change = price_change;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "CurrentPrice [id=" + id + ", symbol=" + symbol + ", price=" + price + ", price_change=" + price_change
				+ ", createdOn=" + createdOn + "]";
	}
	
	
}
