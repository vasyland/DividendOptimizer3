package com.stock.controllers;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.MarketingStatusSymbol;
import com.stock.model.SymbolStatus;
import com.stock.model.VolatilityDay;
import com.stock.services.FeatureService;
import com.stock.services.SymbolService;

@RestController
//@CrossOrigin("*")
//@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:5003")
@RequestMapping("/free")
public class FreePointsController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	SymbolService symbolService;
	@Autowired
	FeatureService featureService;

	@GetMapping("/free-ca-buy-list")
	public @ResponseBody List<SymbolStatus> getFreeCaRecommendedBuySymbols() {
		return symbolService.getCaRecomendedBuySymbols();
	}
	
	@GetMapping("/free-us-buy-list")
	public @ResponseBody List<SymbolStatus> getFreeUsRecommendedBuySymbols() {
		return symbolService.getUsRecomendedBuySymbols();
	}

	@GetMapping("/volatile-days")
	public @ResponseBody List<VolatilityDay> getVolatileDays() {
		return featureService.getActiveEvents();
	}
	
	
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") final String name) {
		return new Greeting(counter.incrementAndGet(), String.format(FreePointsController.template, name));
	}

	@GetMapping("/marketing-ca-list")
	public @ResponseBody List<MarketingStatusSymbol> getCaMarketingSymbols() {
		return symbolService.getCaMarketingStatusSymbols();
	}
	
	@GetMapping("/marketing-us-list")
	public @ResponseBody List<MarketingStatusSymbol> getUsMarketingSymbols() {
		return symbolService.getUsMarketingStatusSymbols();
	}	
	
}
