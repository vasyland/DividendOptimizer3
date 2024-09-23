package com.stock.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.model.MarketingStatusSymbol;
import com.stock.model.SymbolStatus;
import com.stock.repositories.MarketingSymbolStatusRepository;
import com.stock.repositories.SymbolNativeRepository;
import com.stock.repositories.SymbolStatusRepository;

@Service
public class SymbolServiceImpl implements SymbolService {

	private SymbolNativeRepository symbolNativeRepository1;
	private SymbolStatusRepository symbolStatusRepository;
	private MarketingSymbolStatusRepository marketingSymbolStatusRepository;

	public SymbolServiceImpl(SymbolNativeRepository symbolNativeRepository1,
			com.stock.repositories.SymbolStatusRepository symbolStatusRepository,
			MarketingSymbolStatusRepository marketingSymbolStatusRepository
			) {
		super();
		this.symbolNativeRepository1 = symbolNativeRepository1;
		this.symbolStatusRepository = symbolStatusRepository;
		this.marketingSymbolStatusRepository = marketingSymbolStatusRepository;
	}

	@Override
	public List<String> getSymbols() {
		return symbolNativeRepository1.getSymbolForProcessing();
	}

	@Override
	public List<SymbolStatus> getCaRecomendedBuySymbols() {
		
		List<String> actions = Arrays.asList(new String[]{"Buy","Hold"});
		return symbolStatusRepository.getCaSymbolsByRecommendedAction(actions);
	}

	@Override
	public List<SymbolStatus> getUsRecomendedBuySymbols() {
		List<String> actions = Arrays.asList(new String[]{"Buy","Hold"});
		return symbolStatusRepository.getUsSymbolsByRecommendedAction(actions);
	}

	@Override
	public List<MarketingStatusSymbol> getCaMarketingStatusSymbols() {
		return marketingSymbolStatusRepository.getCaMarketingSymbols();
	}

	@Override
	public List<MarketingStatusSymbol> getUsMarketingStatusSymbols() {
		return marketingSymbolStatusRepository.getUsMarketingSymbols();
	}
	
	
}
