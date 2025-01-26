package com.stock.services;

import java.util.List;

import com.stock.model.Scenario;

public interface ScenarioService {
	public List<Scenario> getUserScenarios(long id);
	
	public Scenario findScenarioById(Long id);
	
	public Scenario addScenario(Scenario s);
	
}
