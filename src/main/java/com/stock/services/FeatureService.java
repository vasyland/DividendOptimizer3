package com.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stock.model.VolatilityDay;

@Service
public interface FeatureService {
	
	List<VolatilityDay> getActiveEvents();

}
