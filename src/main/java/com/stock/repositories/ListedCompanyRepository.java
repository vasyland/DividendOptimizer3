package com.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.model.ListedCompany;
import java.util.List;

public interface ListedCompanyRepository extends JpaRepository<ListedCompany, String> {

    List<ListedCompany> findBySymbolStartingWithIgnoreCase(String prefix);

    List<ListedCompany> findByNameStartingWithIgnoreCase(String prefix);
}