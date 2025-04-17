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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "current_price", indexes = {
		@Index(name = "idx_price_symbol", columnList = "symbol")
})
@Getter
@Setter
@NoArgsConstructor
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
}
