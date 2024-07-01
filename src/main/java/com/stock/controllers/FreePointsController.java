package com.stock.controllers;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
@CrossOrigin("*")
@RequestMapping("/free")
public class FreePointsController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	SymbolService symbolService;

	@GetMapping("/free-buy-list")
	public @ResponseBody List<SymbolStatus> getFreeRecommendedBuySymbols() {
		return symbolService.getRecomendedBuySymbols();
	}

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") final String name) {
		return new Greeting(counter.incrementAndGet(), String.format(FreePointsController.template, name));
	}

}
