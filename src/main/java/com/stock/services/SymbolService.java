package com.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.data.SymbolStatusDto;
import com.stock.model.MarketingStatusSymbol;
import com.stock.model.SymbolStatus;

@Service
public interface SymbolService {

	List<String> getSymbols();
	List<SymbolStatus> getCaRecomendedBuySymbols();
	List<SymbolStatus> getUsRecomendedBuySymbols();
	
	List<MarketingStatusSymbol> getCaMarketingStatusSymbols();
	List<MarketingStatusSymbol> getUsMarketingStatusSymbols();
	
	
	//Using watch_symbol and surrent_price tables
	List<SymbolStatusDto> getSymbolStatusList(String symbol);
	
	// Using status table
	List<SymbolStatusDto> getCaSymbolStatusList();
}
