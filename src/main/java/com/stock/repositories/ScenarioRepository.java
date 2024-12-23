package com.stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.Scenario;

@Repository
public interface ScenarioRepository  extends JpaRepository<Scenario, Long> {
	
	/* Find scenario by id */
	Optional<Scenario> findScenarioById(Long id);
	
	/* Find all scenarios for one user */
	@Query(value="select * from scenario where user_id = :userId", nativeQuery=true)
	List<Scenario> findAllScenariosByUserId(@Param("userId") Long userId);
}
