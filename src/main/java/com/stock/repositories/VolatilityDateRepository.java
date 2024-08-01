package com.stock.repositories;

//import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.VolatilityDay;

@Repository
public interface VolatilityDateRepository extends JpaRepository<VolatilityDay, Long>{

//	@Query(value="select * from volatility_date where active = 1 and active_from_date >= :currentDate and active_to_date <= :allowedDate", nativeQuery=true)
//	List<VolatilityDay> getUpcomingEvents(@Param("current_date") Date currentDate, @Param("allowed_date") Date allowedDate);
	@Query(value="select * from volatility_date where active = 1", nativeQuery=true)
	List<VolatilityDay> getUpcomingEvents();
	
	Optional<VolatilityDay> findByActive(Long active);
}
