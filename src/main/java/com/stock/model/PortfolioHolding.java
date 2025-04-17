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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
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
    
}
