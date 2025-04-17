package com.stock.services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PriceServiceImpl implements PriceService {

	private final Map<String, BigDecimal> mockPrices = Map.of(
			"TD.TO", new BigDecimal("45.12"),
			"SHOP.TO", new BigDecimal("152.45"),
			"CVE.TO", new BigDecimal("17.50"));

	@Override
	public BigDecimal getCurrentPrice(String symbol) {
		return mockPrices.getOrDefault(symbol, BigDecimal.ZERO);
	}

}
