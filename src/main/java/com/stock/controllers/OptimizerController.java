package com.stock.controllers;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.stock.model.SymbolStatus;

import com.stock.services.SymbolService;

@RestController
//@CrossOrigin("*")
//@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:5003")
@RequestMapping("/api")
public class OptimizerController {
	
//	private static Logger log = LogManager.getLogger(OptimizerController.class);
  @Autowired
  SymbolService symbolService;
 
//  @CrossOrigin(origins = "http://localhost:5003")
  @GetMapping("/ca-buy-list")
  public @ResponseBody List<SymbolStatus> getCaRecommendedBuySymbols() {
    return symbolService.getCaRecomendedBuySymbols();
  }
  
  @GetMapping("/us-buy-list")
  public @ResponseBody List<SymbolStatus> getUsRecommendedBuySymbols() {
	return symbolService.getUsRecomendedBuySymbols();
  }

}
