package com.stock.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stock.model.SymbolStatus;

import jakarta.persistence.EntityManager;
import com.stock.model.SymbolStatus;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class SymbolNativeRepository {

	@PersistenceContext
	EntityManager entityManager = null;
	
	public List<String> getSymbolForProcessing() {
		//List<String> r = new ArrayList();
//	    TypedQuery<String> sql = entityManager.createQuery("SELECT symbol FROM scenario_details "
//	    		+ "UNION ALL "
//	    		+ "SELECT SYMBOL FROM watch_symbol) T "
//	    		+ "GROUP BY symbol", java.lang.String.class);
	    
//	    TypedQuery<Symbol> sql2 = entityManager.createQuery("SELECT symbol FROM scenario_details", Symbol.class);

//		@Query(value="SELECT SYMBOL FROM ("
//		+ "SELECT symbol FROM scenario_details "
//		+ "UNION ALL "
//		+ "SELECT SYMBOL FROM watch_symbol) T "
//		+ "GROUP BY symbol", nativeQuery=true);
	    
//		Query sql3 = entityManager.createNativeQuery("SELECT SYMBOL FROM ("
//				+ "SELECT symbol FROM watch_symbol "
//				+ "UNION ALL "
//				+ "SELECT symbol FROM position) "
//				+ "T GROUP BY symbol");

		Query sql3 = entityManager.createNativeQuery("select symbol from ("
				+ "select symbol from watch_symbol "
				+ ") "
				+ "t group by symbol");		
		
	    return sql3.getResultList();
	}

	/**
	 * Getting recommended Buy List
	 * @return
	 */
	public List<SymbolStatus> getRecomendedBuySymbols() {
		
		Query sql = entityManager.createNativeQuery("select symbol, "
				+ "current_price,"
				+ "quaterly_dividend_amount, "
				+ "current_yield, "
				+ "upper_yield, "				
				+ "lower_yield, "				
				+ "allowedtobuy_yield, "
				+ "sell_point_yield, "				
				+ "recommended_action, "				
				+ "updated_on "				
				+ "from symbol_status "
		        + "where recommended_action = 'Buy' order by symbol");		
		
	    return sql.getResultList();
	}
	
//	List<Symbol> getSymbolsForProcessing();
}
