package com.stock.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.exceptions.ScenarioNotFoundException;
import com.stock.model.Scenario;
import com.stock.repositories.ScenarioRepository;

@Service
public class ScenarioServiceImpl implements ScenarioService {

	private final ScenarioRepository scenarioRepository;

	public ScenarioServiceImpl(ScenarioRepository scenarioRepository) {
		super();
		this.scenarioRepository = scenarioRepository;
	}
	
	
//	public List<Scenario> getUserScenarios(long id) {
//		return this.scenarioRepository.findAllById(id);
//	}
	
	
	public Scenario findScenarioById(Long id) {
		return scenarioRepository
				.findById(id)
				.orElseThrow(() -> new ScenarioNotFoundException("Sceanrio by id " + id + " was not found"));
	}


	@Override
	public List<Scenario> getUserScenarios(long userId) {
		return  scenarioRepository.findAllScenariosByUserId(userId);
	}


	/** Creating a new user scenario (portfolio) */ 
	@Override
	public Scenario addScenario(Scenario s) {
		
		/* Check if scenario has time stamp */
		if(s.getCreatedOn() == null) {
			LocalDateTime lt = LocalDateTime.now(); 
			s.setCreatedOn(lt);
		}
		return scenarioRepository.save(s);
	}
}
