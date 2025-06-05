package com.stock.data;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PortfolioUnrealizedPnLDto {
	private Long id;
	private BigDecimal unrealizedPnL;
	private BigDecimal realizedPnL;
	private BigDecimal currentCost;
	private int numberOfHoldings;
}