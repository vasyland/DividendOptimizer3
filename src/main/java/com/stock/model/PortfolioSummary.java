package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "portfolio_summary")
@Getter @Setter @NoArgsConstructor
public class PortfolioSummary {

    @Id
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(name = "total_market_value")
    private BigDecimal totalMarketValue;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @Column(name = "realized_pnl")
    private BigDecimal realizedPnL;

    @Column(name = "unrealized_pnl")
    private BigDecimal unrealizedPnL;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
