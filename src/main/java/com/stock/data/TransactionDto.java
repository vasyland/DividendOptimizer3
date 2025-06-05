package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stock.model.Portfolio;
import com.stock.model.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto {

	private Long id;
	private Long portfolio;
	private String symbol;
	private Integer shares;
	private BigDecimal price;
	private BigDecimal commissions;
	private BigDecimal bookCost;
	private BigDecimal Pnl;
	private BigDecimal PnlPercentage;
	private String currency;
	private String transactionType;
	private LocalDateTime transactionDate;
	private String note;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
