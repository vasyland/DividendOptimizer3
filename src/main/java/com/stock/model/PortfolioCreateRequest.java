package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCreateRequest {

//    private Long userId;
    private String name;
    private double initialCash;
    private double currentCash;

}
