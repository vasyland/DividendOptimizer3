package com.stock.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//File: FmpCurrentPriceRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.model.FmpCurrentPrice;
import com.stock.model.FmpCurrentPriceProjection;

@Repository
public interface FmpCurrentPriceRepository extends JpaRepository<FmpCurrentPrice, String> {

 @Query("SELECT f.symbol AS symbol, " +
        "       f.price AS price, " +
        "       f.priceChange AS priceChange, " +
        "       f.createdOn AS createdOn " +
        "FROM FmpCurrentPrice f " +
        "WHERE f.symbol = :symbol")
 Optional<FmpCurrentPriceProjection> findBySymbol(@Param("symbol") String symbol);

 @Query("SELECT f.symbol AS symbol, " +
        "       f.price AS price, " +
        "       f.priceChange AS priceChange, " +
        "       f.createdOn AS createdOn " +
        "FROM FmpCurrentPrice f " +
        "WHERE f.symbol IN :symbols")
 List<FmpCurrentPriceProjection> findBySymbolIn(@Param("symbols") Set<String> symbols);
}