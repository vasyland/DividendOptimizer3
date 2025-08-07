package com.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.model.ListedCompany;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListedCompanyRepository extends JpaRepository<ListedCompany, String> {

    List<ListedCompany> findBySymbolStartingWithIgnoreCase(String prefix);

    List<ListedCompany> findByNameStartingWithIgnoreCase(String prefix);
    
    @Query("SELECT c FROM ListedCompany c WHERE LOWER(c.symbol) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT(:query, '%'))")
    List<ListedCompany> searchBySymbolOrName(@Param("query") String query);
}