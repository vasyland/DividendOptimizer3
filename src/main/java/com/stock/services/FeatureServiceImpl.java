package com.stock.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.model.VolatilityDay;
import com.stock.repositories.VolatilityDateRepository;
import com.stock.security.service.AuthService;
import com.stock.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeatureServiceImpl implements FeatureService {
	
	@Autowired
	private VolatilityDateRepository volatilityDateRepository;

	/**
	 * Get all registered events from the database (testing only)
	 */
	@Override
	public List<VolatilityDay> getActiveEvents() {
		
		
		/* 1. Get start view date and a date of two weeks ahead */
		String viewStartDate = DateUtil.getShiftedDate(-1); //-2
		String viewEndDate = DateUtil.getShiftedDate(12);  //14
		
		log.info("viewStartDate = " + viewStartDate);
		log.info("viewEndDate = " + viewEndDate);
				
		return volatilityDateRepository.getUpcomingEvents(viewStartDate, viewEndDate);
		
//		return volatilityDateRepository.findAll();
	}
}
