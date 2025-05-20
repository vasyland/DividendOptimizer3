package com.stock.model;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioUpdateRequest {

	private Long id;
	private String name;
	private BigDecimal initialCash;
	private BigDecimal currentCash;
	
}
