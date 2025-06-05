package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.SymbolStatus;

@Repository
public interface SymbolStatusRepository extends JpaRepository<SymbolStatus, String> {

	/* Getting CA companies only with .TO */
	@Query(value="select * from symbol_status where symbol like '%.TO' and recommended_action in (:action)", nativeQuery=true)
	List<SymbolStatus> getCaSymbolsByRecommendedAction(@Param("action") List<String> action);
	
	/* Getting US companies only without .TO */
	@Query(value="select * from symbol_status where symbol not like '%.TO' and recommended_action in (:action)", nativeQuery=true)
	List<SymbolStatus> getUsSymbolsByRecommendedAction(@Param("action") List<String> action);
	
	/* Getting CA companies only with .TO */
	@Query(value="select * from symbol_status where symbol like '%.TO'", nativeQuery=true)
	List<SymbolStatus> getCaSymbols();
}
