package com.stock.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stock.model.Scenario;
import com.stock.model.SymbolStatus;
import com.stock.services.ScenarioService;
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
  @Autowired
  ScenarioService scenarioService;
 
  @CrossOrigin(origins = "http://localhost:5004")
  @GetMapping("/ca-buy-list")
  public @ResponseBody List<SymbolStatus> getCaRecommendedBuySymbols() {
    return symbolService.getCaRecomendedBuySymbols();
  }
  
  @CrossOrigin(origins = "http://localhost:5004")
  @GetMapping("/us-buy-list")
  public @ResponseBody List<SymbolStatus> getUsRecommendedBuySymbols() {
	return symbolService.getUsRecomendedBuySymbols();
  }
  
//  @CrossOrigin(origins = "http://localhost:5004")
//  @GetMapping("/scenario/{userId}")
//  public ResponseEntity<List<Scenario>> getRecordsByUserId(@PathVariable Long userId) {
//      List<Scenario> scenarios = scenarioService.   getUserScenarios(userId);
//      if (scenarios.isEmpty()) {
//          return ResponseEntity.notFound().build();
//      }
//      return ResponseEntity.ok(scenarios);
//  }
  
    
  @CrossOrigin(origins = "http://localhost:5004")
  @GetMapping("/scenario/user/{userId}")
	public ResponseEntity<List<Scenario>> getUserScenarios(@PathVariable("userId") Long userId) {
	  List<Scenario> scenarios = scenarioService.getUserScenarios(userId);
		if (scenarios == null) {
		      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found");
		}
		return new ResponseEntity<>(scenarios, HttpStatus.OK);
	}
  
//  @CrossOrigin(origins = "http://localhost:5004")
//  @GetMapping("/scenario/{id}")
//	public ResponseEntity<Scenario> getScenariById(@PathVariable("id") Long id) {
//		Scenario scenario = scenarioService.findScenarioById(id);
//		if (scenario == null) {
//		      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found");
//		}
//		return new ResponseEntity<>(scenario, HttpStatus.OK);
//	}
  
  
  @PostMapping("/scenario/add")
  public ResponseEntity<Scenario> addScenario(@RequestBody Scenario s) {
	Scenario scenario = scenarioService.addScenario(s);
	return new ResponseEntity<>(scenario, HttpStatus.CREATED);
  }
  
}
