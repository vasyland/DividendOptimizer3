package com.stock.controllers;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  @GetMapping("/buy-list")
  public @ResponseBody List<SymbolStatus> getRecommendedBuySymbols() {
    return symbolService.getRecomendedBuySymbols();
  }

}
