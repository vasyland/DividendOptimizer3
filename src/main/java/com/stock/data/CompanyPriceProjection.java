package com.stock.data;

import java.math.BigDecimal;

public interface CompanyPriceProjection {
    String getSymbol();
    String getName();
    BigDecimal getPrice();
    BigDecimal getPriceChange();
}
