package com.stock.controllers;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.data.SymbolStatusDto;
import com.stock.services.SymbolService;

@RestController
@RequestMapping("/api")
public class OptimizerController {
	
  private static final Logger log = LoggerFactory.getLogger(OptimizerController.class);
  
  @Autowired
  SymbolService symbolService;
 
  @CrossOrigin(origins = "http://localhost:5004")
  @GetMapping("/ca-buy-list")
  public List<SymbolStatusDto> getCaRecommendedBuySymbols() {  //@ResponseBody
	List<String> exchanges = Arrays.asList("TSX");
    return symbolService.getSymbolStatusList(exchanges);
  }
  
  
  @CrossOrigin(origins = "http://localhost:5004")
  @GetMapping("/us-buy-list")
  public List<SymbolStatusDto> getUsRecommendedBuySymbols() {  //@ResponseBody
	List<String> exchanges = Arrays.asList("NYSE","NASDAQ");
	return symbolService.getSymbolStatusList(exchanges);
  }
}
