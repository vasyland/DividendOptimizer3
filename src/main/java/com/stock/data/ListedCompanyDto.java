package com.stock.data;

public class ListedCompanyDto {

    private String symbol;
    private String name;
    private String exchange;
    private String marketcapFormatted;
    
	public ListedCompanyDto() {
		super();
	}

	public ListedCompanyDto(String symbol, String name, String exchange, String marketcapFormatted) {
		super();
		this.symbol = symbol;
		this.name = name;
		this.exchange = exchange;
		this.marketcapFormatted = marketcapFormatted;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getMarketcapFormatted() {
		return marketcapFormatted;
	}
	public void setMarketcapFormatted(String marketcapFormatted) {
		this.marketcapFormatted = marketcapFormatted;
	}
	@Override
	public String toString() {
		return "ListedCompanyDto [symbol=" + symbol + ", name=" + name + ", exchange=" + exchange
				+ ", marketcapFormatted=" + marketcapFormatted + "]";
	}    
}
