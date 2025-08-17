package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stock.data.SymbolStatusDto;
import com.stock.model.FmpCurrentPriceProjection;
import com.stock.model.WatchSymbol;
import com.stock.repositories.FmpCurrentPriceRepository;
import com.stock.repositories.SymbolNativeRepository;
import com.stock.repositories.WatchSymbolRepository;

@Service
public class SymbolServiceImpl implements SymbolService {
	
	private static final Logger log = LoggerFactory.getLogger(SymbolServiceImpl.class);
	
	private WatchSymbolRepository watchSymbolRepository;
	private final FmpCurrentPriceRepository fmpCurrentPriceRepository;
	private SymbolNativeRepository symbolNativeRepository1;
	
	public SymbolServiceImpl(WatchSymbolRepository watchSymbolRepository, 
			FmpCurrentPriceRepository fmpCurrentPriceRepository, 
			SymbolNativeRepository symbolNativeRepository1
			) {
		super();
		this.watchSymbolRepository = watchSymbolRepository;
		this.fmpCurrentPriceRepository = fmpCurrentPriceRepository;
		this.symbolNativeRepository1 = symbolNativeRepository1;
		
	}

	@Override
	public List<String> getSymbols() {
		return symbolNativeRepository1.getSymbolForProcessing();
	}
	
	
	/**
	 * Canadian and US Dividend Buying Lists
	 */
	@Override
	public List<SymbolStatusDto> getSymbolStatusList(List<String> exchanges) {

		log.info("[SymbolServiceImpl.getSymbolStatusList] : #100 exchanges = " + exchanges.toString() );
		
		//Getting watch symbol list for TSX
		Set<WatchSymbol> watchSymbols = watchSymbolRepository.findByExchangeIn(exchanges);
		
		if (watchSymbols == null || watchSymbols.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for exchange: " + exchanges);
		}
		
		Set<String> symbols = this.extractDistinctSymbols(watchSymbols);
		
		// Get current prices for the selected watch symbols
		List<FmpCurrentPriceProjection> priceDataList = fmpCurrentPriceRepository.findBySymbolIn(symbols);
		
		List<SymbolStatusDto> symbolStatusList = new ArrayList<>();
		
		// Recommended Actions
		int res;
		int res2;
		int res3;
		String action = "";
		
		//Looping via watch symbols and calculating status
		for(WatchSymbol w : watchSymbols ) {
			
			FmpCurrentPriceProjection price = priceDataList.stream()
                    .filter(cp -> cp.getSymbol().equals(w.getSymbol()))
                    .findFirst()
                    .orElse(null);
			
				if (price == null) {
					log.warn("No current price found for symbol: " + w.getSymbol());
					continue; // Skip this symbol if no price is found
				}

				BigDecimal currentYield = w.getQuoterlyDividendAmount().multiply(BigDecimal.valueOf(400))
						.divide(price.getPrice(), 3, RoundingMode.HALF_EVEN);
				BigDecimal yieldRange = w.getUpperYield().subtract(w.getLowerYield());
				BigDecimal quoterOfUpperYield =  yieldRange.divide(BigDecimal.valueOf(6), 3, RoundingMode.HALF_EVEN);
				BigDecimal allowedBuyYield = w.getUpperYield().subtract(quoterOfUpperYield);
				BigDecimal allowedBuyPrice = w.getQuoterlyDividendAmount().multiply(BigDecimal.valueOf(400)).divide(allowedBuyYield, 2, RoundingMode.HALF_EVEN);
				BigDecimal bestBuyPrice = w.getQuoterlyDividendAmount().multiply(BigDecimal.valueOf(400)).divide(w.getUpperYield(), 2, RoundingMode.HALF_EVEN);

				// Calculating sell price base low yield and quarterly dividend amount
				BigDecimal sellPrice = w.getQuoterlyDividendAmount().multiply(BigDecimal.valueOf(400))
						.divide(w.getLowerYield(), RoundingMode.HALF_EVEN);
				// Calculating overpriced amount
				BigDecimal overpricedAmount = price.getPrice().subtract(bestBuyPrice).setScale(4, RoundingMode.HALF_UP);
				
				// Calculating overpriced percentage
				BigDecimal overpricedPercentage = (price.getPrice().subtract(bestBuyPrice))
						.divide(price.getPrice(), 4, RoundingMode.HALF_EVEN) // Scale set to 4 for precision
						.multiply(BigDecimal.valueOf(100))
						.setScale(2, RoundingMode.HALF_EVEN);
				
				BigDecimal sellPointYield = w.getLowerYield().add(quoterOfUpperYield);

				// Determine recommended action based on current yield and allowed buy yield
				res = currentYield.compareTo(allowedBuyYield);
			    res2 = w.getUpperYield().compareTo(BigDecimal.valueOf(0.0));
			    if (res == 0 || res == 1 && res2 != 0) {
			       action = "Buy";
			    } else {
			       action = "";
			    }
			    
			    res3 = currentYield.compareTo(sellPointYield);
			    if (res3 == -1 && res2 != 0) {
				   action = "Sell";
			    }
				
			    if(res == -1 && res3 == 1 && res2 != 0) {
			    	action = "Hold";
			    }
				
				// Creating SymbolStatusDto object to hold the status information
				SymbolStatusDto symbolStatus = new SymbolStatusDto();
				
				symbolStatus.setSymbol(w.getSymbol());
				symbolStatus.setCurrentPrice(price.getPrice());
				symbolStatus.setCurrentYield(currentYield);
				symbolStatus.setLowerYield(w.getLowerYield());
				symbolStatus.setUpperYield(w.getUpperYield());
				symbolStatus.setAllowedBuyYield(allowedBuyYield);
				symbolStatus.setAllowedBuyPrice(allowedBuyPrice);
				symbolStatus.setBestBuyPrice(bestBuyPrice);
				symbolStatus.setQuoterlyDividendAmount(w.getQuoterlyDividendAmount());
				symbolStatus.setSellPrice(sellPrice);
				symbolStatus.setOverpricedAmount(overpricedAmount);
				symbolStatus.setOverpricedPercentage(overpricedPercentage);
				symbolStatus.setSellPointYield(sellPointYield);
			    symbolStatus.setRecommendedAction(action);
			    symbolStatus.setUpdatedOn(price.getCreatedOn());

			    symbolStatusList.add(symbolStatus);
		}
		return symbolStatusList;
	}
	
	
	/** Extracts distinct symbols from a set of WatchSymbol objects.
	 * This method filters out null or blank symbols and collects the unique symbols into a Set.	
	 * @param watchSymbols
	 * @return
	 */
	public Set<String> extractDistinctSymbols(Set<WatchSymbol> watchSymbols) {
		return watchSymbols.stream()
				.map(WatchSymbol::getSymbol)
				.filter(symbol -> symbol != null && !symbol.isBlank())
				.collect(Collectors.toSet());
	}	
}
