package com.stock.repositories;

import java.util.List;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class SymbolNativeRepository {

	@PersistenceContext
	EntityManager entityManager = null;
	
	public List<String> getSymbolForProcessing() {

		Query sql3 = entityManager.createNativeQuery("select symbol from ("
				+ "select symbol from watch_symbol "
				+ ") "
				+ "t group by symbol");		
		
	    return sql3.getResultList();
	}
}
