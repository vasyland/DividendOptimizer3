package com.stock.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PortfolioSummaryDTO {
	
	private Long portfolioId;
	private BigDecimal cash;
	private BigDecimal marketValue;
	private BigDecimal totalValue;
	private BigDecimal realizedPnL;
	private BigDecimal unrealizedPnL;

	public PortfolioSummaryDTO(Long portfolioId,
			BigDecimal cash,
			BigDecimal marketValue,
			BigDecimal totalValue,
			BigDecimal realizedPnL,
			BigDecimal unrealizedPnL) {
		this.portfolioId = portfolioId;
		this.cash = cash;
		this.marketValue = marketValue;
		this.totalValue = totalValue;
		this.realizedPnL = realizedPnL;
		this.unrealizedPnL = unrealizedPnL;
	}
}
