package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateRequest {

	private Long id;
	private String symbol;
	private Integer shares;
	private BigDecimal price;
	private BigDecimal commissions;
	private String currency;
	private TransactionType transactionType;
	private LocalDateTime transactionDate;
	private String note;
}
