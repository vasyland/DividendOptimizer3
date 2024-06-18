package com.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.model.SymbolStatus;

@Service
public interface SymbolService {

	List<String> getSymbols();
	List<SymbolStatus> getRecomendedBuySymbols();
}
