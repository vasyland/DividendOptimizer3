package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.SymbolStatus;

@Repository
public interface SymbolStatusRepository extends JpaRepository<SymbolStatus, String> {

	@Query(value="select * from symbol_status where recommended_action in (:action)", nativeQuery=true)
	List<SymbolStatus> getSymbolsByRecommendedAction(@Param("action") List<String> action);

//	@Query(value="select * from symbol_status where recommended_action in (:action)", nativeQuery=true)
//	List<SymbolStatus> getSymbolsByRecommendedAction(@Param("action") String action);
	
	
}
