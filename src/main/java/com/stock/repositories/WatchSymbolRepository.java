package com.stock.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.WatchSymbol;

@Repository
public interface WatchSymbolRepository extends JpaRepository<WatchSymbol, String>{
	Set<WatchSymbol> findByExchangeIn(List<String> exchanges);
}
