package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stock.model.MarketingStatusSymbol;

@Repository
public interface MarketingSymbolStatusRepository extends JpaRepository<MarketingStatusSymbol, String> {

	/* Getting CA companies only with .TO */
	@Query(value="select * from marketing_symbol_status where symbol like '%.TO'", nativeQuery=true)
	List<MarketingStatusSymbol> getCaMarketingSymbols();
	
	/* Getting US companies only without .TO */
	@Query(value="select * from marketing_symbol_status where symbol not like '%.TO'", nativeQuery=true)
	List<MarketingStatusSymbol> getUsMarketingSymbols();
	
}
