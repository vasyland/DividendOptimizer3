package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SymbolStatusDto {

	private String symbol;  
	private BigDecimal currentPrice;
	private BigDecimal currentYield;
	private BigDecimal lowerYield;
	private BigDecimal upperYield;
	private BigDecimal allowedBuyPrice;
	private BigDecimal allowedBuyYield;
	private BigDecimal bestBuyPrice;
	private BigDecimal quoterlyDividendAmount;
	private BigDecimal sellPointYield;
	private BigDecimal overpricedAmount;
	private BigDecimal overpricedPercentage;
	private BigDecimal sellPrice;
	private LocalDateTime updatedOn;
	private String  recommendedAction;
	
}
