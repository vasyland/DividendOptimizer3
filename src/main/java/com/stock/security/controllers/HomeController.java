package com.stock.security.controllers;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
	
	@GetMapping("/process")
	public String process() {
		return "Business is in process...";
	}
	
	@GetMapping("/info")
	public String showUser() {
		return "USER PRINCIPAL: " ;
	}
}
