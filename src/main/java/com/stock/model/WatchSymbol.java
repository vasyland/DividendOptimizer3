package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@Table(name = "watch_symbol", uniqueConstraints = {@UniqueConstraint(columnNames = "symbol")})
public class WatchSymbol implements java.io.Serializable {

  private static final long serialVersionUID = -2952735933715107255L;

  @Id
  @Column(name = "symbol", unique = true, nullable = false, length = 10)
  private String symbol;
  @Column(name = "quoterly_dividend_amount")
  private BigDecimal quoterlyDividendAmount;
  @Column(name = "upper_yield")
  private BigDecimal upperYield;
  @Column(name = "lower_yield")
  private BigDecimal lowerYield;
  @Column(name = "exchange", length = 10) 
  private String exchange;
  @Column(name = "updated_on")
  private LocalDateTime updatedOn;

}
