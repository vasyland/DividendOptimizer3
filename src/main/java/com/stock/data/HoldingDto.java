package com.stock.data;

import java.math.BigDecimal;

public record HoldingDto(
		String symbol,
		int shares,
		BigDecimal averageCost,
		BigDecimal currentPrice,
		BigDecimal marketValue,
		BigDecimal totalCost,
		BigDecimal profitLoss,
		String currency) {
}