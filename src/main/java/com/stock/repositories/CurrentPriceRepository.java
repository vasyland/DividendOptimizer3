package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.CurrentPrice;

@Repository
public interface CurrentPriceRepository extends JpaRepository<CurrentPrice, Integer> {
	List<CurrentPrice> findBySymbol(String symbol);
	CurrentPrice findFirstBySymbol(String symbol);
	CurrentPrice findTopBySymbolOrderByCreatedOnDesc(String symbol);
}
