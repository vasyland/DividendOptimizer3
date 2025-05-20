package com.stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.CurrentPrice;

@Repository
public interface CurrentPriceRepository extends JpaRepository<CurrentPrice, Integer> {
	List<CurrentPrice> findBySymbol(String symbol);
	CurrentPrice findFirstBySymbol(String symbol);

	CurrentPrice findTopBySymbolOrderByCreatedOnDesc(String symbol);
	
	 @Query(value = """
		        SELECT cp.*
		        FROM (
		            SELECT *,
		                   ROW_NUMBER() OVER (PARTITION BY symbol ORDER BY created_on DESC) AS rn
		            FROM current_price
		            WHERE symbol IN (:symbols)
		        ) cp
		        WHERE cp.rn = 1
		        """, nativeQuery = true)
	 List<CurrentPrice> findLatestPricesForSymbols(@Param("symbols") List<String> symbols);
	 
	 @Query("SELECT c FROM CurrentPrice c WHERE c.symbol = :symbol ORDER BY c.createdOn DESC")
	 Optional<CurrentPrice> findLatestBySymbol(String symbol);
	 
	 List<CurrentPrice> findBySymbolIn(List<String> symbols);
}
