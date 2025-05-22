package com.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * This class is to bring all information about Portfolio upfront.
 */

@Getter
@Setter
@NoArgsConstructor
public class PortfolioDto {
    
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal initialCash;
    private BigDecimal currentCash;  
    private BigDecimal currentCost;
    private BigDecimal realizedPnL;
    private BigDecimal unrealizedPnL;
    private BigDecimal totalValue;
    private int numberOfholdings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    
}
