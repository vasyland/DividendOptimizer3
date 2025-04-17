package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	@JsonIgnore
	private Portfolio portfolio; // References Portfolio entity

	@Column(nullable = false, length = 10)
	private String symbol;

	@Column(nullable = false)
	private Integer shares;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "commissions")
	private BigDecimal commissions;

	@Column(name = "currency")
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 4)
	private TransactionType transactionType;

	@Column(name = "transaction_date")
	private LocalDateTime transactionDate;

	@Column(name = "note")
	private String note;

	@Column(name = "created_at", updatable = false)
	@JsonIgnore
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonIgnore
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (transactionDate == null) {
			transactionDate = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
