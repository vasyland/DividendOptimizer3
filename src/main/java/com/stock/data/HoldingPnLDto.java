package com.stock.data;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
public class HoldingPnLDto {
    private Long id;
    private Long portfolioId;
    private String symbol;
    private Integer shares;
    private BigDecimal avgCostPerShare;
    private BigDecimal bookCost;
    private BigDecimal unrealizedPnL;
    private BigDecimal realizedPnL;    
    private BigDecimal currentCost;
    private String currency;
}