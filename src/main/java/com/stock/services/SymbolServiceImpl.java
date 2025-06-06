package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stock.data.SymbolStatusDto;
import com.stock.model.CurrentPrice;
import com.stock.model.MarketingStatusSymbol;
import com.stock.model.SymbolStatus;
import com.stock.model.WatchSymbol;
import com.stock.repositories.CurrentPriceRepository;
import com.stock.repositories.MarketingSymbolStatusRepository;
import com.stock.repositories.SymbolNativeRepository;
import com.stock.repositories.SymbolStatusRepository;
import com.stock.repositories.WatchSymbolRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SymbolServiceImpl implements SymbolService {
	
	private static final Logger log = LoggerFactory.getLogger(SymbolServiceImpl.class);
	
	@Autowired
	private WatchSymbolRepository watchSymbolRepository;
	@Autowired
	private CurrentPriceRepository currentPriceRepository;
	
	private SymbolNativeRepository symbolNativeRepository1;
	private SymbolStatusRepository symbolStatusRepository;
	private MarketingSymbolStatusRepository marketingSymbolStatusRepository;
	
	public SymbolServiceImpl(WatchSymbolRepository watchSymbolRepository, CurrentPriceRepository currentPriceRepository,
			SymbolNativeRepository symbolNativeRepository1, SymbolStatusRepository symbolStatusRepository,
			MarketingSymbolStatusRepository marketingSymbolStatusRepository) {
		super();
		this.watchSymbolRepository = watchSymbolRepository;
		this.currentPriceRepository = currentPriceRepository;
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
		
		List<String> actions = Arrays.asList(new String[]{"Buy"});  //,"Hold"
		return symbolStatusRepository.getCaSymbolsByRecommendedAction(actions);
	}

	@Override
	public List<SymbolStatus> getUsRecomendedBuySymbols() {
		List<String> actions = Arrays.asList(new String[]{"Buy"}); // ,"Hold"
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
	
	
	/** 
	 * Getting CA companies only with .TO
	 */
	@Override
	public List<SymbolStatusDto> getCaSymbolStatusList() {
		
		List<SymbolStatus> caList = symbolStatusRepository.getCaSymbols();
		log.info("CA List size: " + caList.size());
		
		if (caList == null || caList.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found");
		}
		
		List<SymbolStatusDto> caSymbolStatusList = new ArrayList<>();
		
		for (SymbolStatus symbolStatus : caList) {
			
			SymbolStatusDto symbolStatusDto = new SymbolStatusDto();
			
			symbolStatusDto.setSymbol(symbolStatus.getSymbol());
			symbolStatusDto.setCurrentPrice(symbolStatus.getCurrentPrice());
			symbolStatusDto.setCurrentYield(symbolStatus.getCurrentYield());
			symbolStatusDto.setLowerYield(symbolStatus.getLowerYield());
			symbolStatusDto.setUpperYield(symbolStatus.getUpperYield());
			symbolStatusDto.setAllowedBuyPrice(symbolStatus.getAllowedBuyPrice());
			symbolStatusDto.setAllowedBuyYield(symbolStatus.getAllowedBuyYield());
			symbolStatusDto.setBestBuyPrice(symbolStatus.getBestBuyPrice());
			symbolStatusDto.setQuoterlyDividendAmount(symbolStatus.getQuoterlyDividendAmount());
			symbolStatusDto.setSellPointYield(symbolStatus.getSellPointYield());
			symbolStatusDto.setUpdatedOn(symbolStatus.getUpdatedOn());
			symbolStatusDto.setRecommendedAction(symbolStatus.getRecommendedAction());
			
//			// Calculating sell price base low yeild and quaterly dividend amount
			BigDecimal sellPrice = symbolStatus.getQuoterlyDividendAmount().multiply(BigDecimal.valueOf(400)).divide(symbolStatus.getLowerYield(), RoundingMode.HALF_EVEN);
			//Calculating overpriced amount
			BigDecimal overpricedAmount = symbolStatus.getCurrentPrice().subtract(symbolStatus.getBestBuyPrice());
			
			// Calculating overpriced percentage
			BigDecimal overpricedPercentage = (symbolStatus.getCurrentPrice().subtract(symbolStatus.getBestBuyPrice()))
				    .divide(symbolStatus.getCurrentPrice(), 4, RoundingMode.HALF_EVEN) // Scale set to 4 for precision
				    .multiply(BigDecimal.valueOf(100))
				    .setScale(2, RoundingMode.HALF_EVEN);
			
			symbolStatusDto.setSellPrice(sellPrice);
			symbolStatusDto.setOverpricedAmount(overpricedAmount);
			symbolStatusDto.setOverpricedPercentage(overpricedPercentage);
			
			caSymbolStatusList.add(symbolStatusDto);
		}
		return caSymbolStatusList;
	}

	/**
	 * 
	 */
	@Override
	public List<SymbolStatusDto> getSymbolStatusList(String exchange) {

		//Getting watch symbol list for TSX
		Set<WatchSymbol> watchSymbols = watchSymbolRepository.findByExchange(exchange);
		
		if (watchSymbols == null || watchSymbols.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for exchange: " + exchange);
		}
		
		Set<String> symbols = this.extractDistinctSymbols(watchSymbols);
		
		// Get current prices for the selected watch symbols
		List<CurrentPrice> priceList = currentPriceRepository.findBySymbolIn(symbols);
		
		List<SymbolStatusDto> symbolStatusList = new ArrayList<>();
		
		// Recommended Actions
		int res;
		int res2;
		int res3;
		String action = "";
		
		//Looping via watch symbols and calculating status
		for(WatchSymbol w : watchSymbols ) {
			
			CurrentPrice price = priceList.stream()
					.filter(t -> t.getSymbol().equals(w.getSymbol()))
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
				symbolStatus.setUpdatedOn(price.getCreatedOn());
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
