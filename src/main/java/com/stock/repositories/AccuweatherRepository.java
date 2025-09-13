package com.stock.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.model.Accuweather;

public interface AccuweatherRepository extends JpaRepository<Accuweather, Integer> {

    @Query("SELECT a FROM Accuweather a WHERE a.createdOn BETWEEN :startDate AND :endDate")
    List<Accuweather> findAllByCreatedOnBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
