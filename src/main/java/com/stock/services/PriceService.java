package com.stock.services;

import java.math.BigDecimal;

public interface PriceService {
	BigDecimal getCurrentPrice(String symbol);
}
