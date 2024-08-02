package com.stock.repositories;

//import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.VolatilityDay;

@Repository
public interface VolatilityDateRepository extends JpaRepository<VolatilityDay, Long>{

	@Query(value="select * from volatility_date where active = 1 and day_date >= :start_date and day_date <= :end_date order by day_date", nativeQuery=true)
	List<VolatilityDay> getUpcomingEvents(@Param("start_date") String startDate, @Param("end_date") String endDate);

//	@Query(value="select * from volatility_date where active = 1", nativeQuery=true)
//	List<VolatilityDay> getUpcomingEvents();
	
//	Optional<VolatilityDay> findByActive(Long active);
}
