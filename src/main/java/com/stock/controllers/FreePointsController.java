package com.stock.controllers;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.data.CompanyPriceProjection;
import com.stock.data.ListedCompanyDto;
import com.stock.model.VolatilityDay;
import com.stock.services.CompanyPriceService;
import com.stock.services.FeatureService;
import com.stock.services.ListedCompanyService;
import com.stock.services.SymbolService;

@RestController
//@CrossOrigin("*")
//@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:5003")
@RequestMapping("/free")
public class FreePointsController {
	
	private static final Logger log = LoggerFactory.getLogger(FreePointsController.class);

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private final SymbolService symbolService;
	private final FeatureService featureService;
	private final ListedCompanyService listedCompanyService;
	private final CompanyPriceService service;

	
	public FreePointsController(SymbolService symbolService, FeatureService featureService,
			ListedCompanyService listedCompanyService, CompanyPriceService service) {
		this.symbolService = symbolService;
		this.featureService = featureService;
		this.listedCompanyService = listedCompanyService;
		this.service = service;
	}

	@GetMapping("/company-prices")
    public List<CompanyPriceProjection> getLatestCompanyPrices() {
        return service.getLatestPrices();
    }
	
	@GetMapping("/all-companies")
	public List<ListedCompanyDto> getAllCompanies() {
	        return listedCompanyService.getAllCompanies();
	}
	
	/**
	 * Search companies by symbol or name.
	 * http://localhost:8080/free/companies/search?query=BMO
	 * @param query the search query
	 * @return a list of companies matching the query
	 */
	@GetMapping("/companies/search")
	public List<ListedCompanyDto> searchCompanies(@RequestParam("query") String query) {
	    return listedCompanyService.searchBySymbolOrName(query);
	}
		
	@GetMapping("/searchBySymbol")
    public List<ListedCompanyDto> searchBySymbol(@RequestParam("prefix") String prefix) {
        return listedCompanyService.searchBySymbolPrefix(prefix);
    }

   
    @GetMapping("/searchByName")
    public List<ListedCompanyDto> searchByName(@RequestParam("prefix") String prefix) {
        return listedCompanyService.searchByNamePrefix(prefix);
    }
	

	@GetMapping("/volatile-days")
	public @ResponseBody List<VolatilityDay> getVolatileDays() {
		return featureService.getActiveEvents();
	}
	
	
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") final String name) {
		return new Greeting(counter.incrementAndGet(), String.format(FreePointsController.template, name));
	}
	
}
