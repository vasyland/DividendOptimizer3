// File: FmpCurrentPriceProjection.java
package com.stock.model; // Adjust package as needed

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FmpCurrentPriceProjection {
    String getSymbol();
    BigDecimal getPrice();
    BigDecimal getPriceChange();
    LocalDateTime getCreatedOn();
}