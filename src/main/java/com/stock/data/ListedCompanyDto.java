package com.stock.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListedCompanyDto {

    private String symbol;
    private String name;
    private String exchange;
    private String marketcapFormatted;
}
