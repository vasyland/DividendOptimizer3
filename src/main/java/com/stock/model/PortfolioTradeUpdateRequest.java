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
public class PortfolioTradeUpdateRequest {
    
    private Long id;  // Transaction ID
    private String symbol;
    private Integer shares;
    private BigDecimal price;
    private BigDecimal commissions;
    private String currency;
    private TransactionType operation;
    private LocalDateTime tradeDate;
    private String note;
}
