package com.stock.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.model.SymbolStatus;
import com.stock.repositories.SymbolNativeRepository;
import com.stock.repositories.SymbolStatusRepository;

@Service
public class SymbolServiceImpl implements SymbolService {

	private SymbolNativeRepository symbolNativeRepository1;
	private SymbolStatusRepository symbolStatusRepository;

	public SymbolServiceImpl(SymbolNativeRepository symbolNativeRepository1,
			com.stock.repositories.SymbolStatusRepository symbolStatusRepository) {
		super();
		this.symbolNativeRepository1 = symbolNativeRepository1;
		this.symbolStatusRepository = symbolStatusRepository;
	}

	@Override
	public List<String> getSymbols() {
		return symbolNativeRepository1.getSymbolForProcessing();
	}

	@Override
	public List<SymbolStatus> getRecomendedBuySymbols() {
		
		List<String> actions = Arrays.asList(new String[]{"Buy","Hold"});
		return symbolStatusRepository.getSymbolsByRecommendedAction(actions);
	}
}
