package com.stock.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.stock.data.SymbolStatusDto;

@Service
public interface SymbolService {

	List<String> getSymbols();
	
	//Using watch_symbol and current_price tables
	List<SymbolStatusDto> getSymbolStatusList(List<String> exchanges);

}
