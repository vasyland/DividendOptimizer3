package com.stock.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.stock.security.entity.UserInfo;

@Entity
@Table(name = "portfolio")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserInfo user; // References UserInfo entity

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "initial_cash", nullable = false, precision = 15, scale = 2)
    private BigDecimal initialCash;

    @Column(name = "current_cash", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentCash;    
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getInitialCash() {
		return initialCash;
	}

	public void setInitialCash(BigDecimal initialCash) {
		this.initialCash = initialCash;
	}

	public BigDecimal getCurrentCash() {
		return currentCash;
	}

	public void setCurrentCash(BigDecimal currentCash) {
		this.currentCash = currentCash;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Portfolio [id=" + id + ", user=" + user + ", name=" + name + ", initialCash=" + initialCash
				+ ", currentCash=" + currentCash + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
    
    
}
