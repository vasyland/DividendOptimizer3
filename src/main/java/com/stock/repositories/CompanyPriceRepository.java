package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.stock.data.CompanyPriceProjection;
import com.stock.model.ListedCompany;

@Repository
public interface CompanyPriceRepository extends CrudRepository<ListedCompany, String> {

    @Query(value = """
        SELECT lc.symbol, lc.name, cp.price, cp.price_change
        FROM listed_companies lc
        JOIN (
            SELECT *
            FROM (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY symbol ORDER BY created_on DESC) AS rn
                FROM current_price
            ) ranked
            WHERE rn = 1
        ) cp ON lc.symbol = cp.symbol
        """, nativeQuery = true)
    List<CompanyPriceProjection> getLatestCompanyPrices();
}
