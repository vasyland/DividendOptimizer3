package com.stock.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.model.VolatilityDay;
import com.stock.repositories.VolatilityDateRepository;

@Service
public class FeatureServiceImpl implements FeatureService {
	
	@Autowired
	private VolatilityDateRepository volatilityDateRepository;

	/**
	 * Get all registered events from the database (testing only)
	 */
	@Override
	public List<VolatilityDay> getActiveEvents() {
		return volatilityDateRepository.findAll();
	}
}
