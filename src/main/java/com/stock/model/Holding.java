package com.stock.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holding", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"portfolio_id", "symbol"})
})
@Getter
@Setter
@NoArgsConstructor
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false)
    private Integer shares;

    @Column(name = "avg_cost_per_share", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgCostPerShare;

    @Column(name = "book_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal bookCost;
    
    @Column(name = "realized_pnl")
    private BigDecimal realizedPnL;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
