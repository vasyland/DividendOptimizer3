package com.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "listed_companies", indexes = {
    @Index(name = "idx_listed_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListedCompany {

    @Id
    @Column(length = 11)
    private String symbol;  // PRIMARY KEY

    @Column(length = 100)
    private String name;

    @Column
    private Long marketcap;

    @Column(length = 6)
    private String exchange;
}
