package com.stock.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "volatility_date", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class VolatilityDay implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false, updatable=false)
	private Long id;
	
	@Column(name = "day_date")
	@CreationTimestamp
	private LocalDateTime dayDate;
	
	@Column(name = "action_description")
	private String actionDescription;
	
	@Column(name = "description")
	private String description;
	
	@Column(name="active")
	private Long active;
	
	@Column(name = "active_from_date")
	@CreationTimestamp
	private LocalDateTime activeFromDate;
	
	@Column(name = "active_to_date")
	@CreationTimestamp
	private LocalDateTime activeToDate;
	
	@Column(name = "created_on")
	@CreationTimestamp
	private LocalDateTime createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private LocalDateTime updatedOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDayDate() {
		return dayDate;
	}

	public void setDayDate(LocalDateTime dayDate) {
		this.dayDate = dayDate;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}

	public LocalDateTime getActiveFromDate() {
		return activeFromDate;
	}

	public void setActiveFromDate(LocalDateTime activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	public LocalDateTime getActiveToDate() {
		return activeToDate;
	}

	public void setActiveToDate(LocalDateTime activeToDate) {
		this.activeToDate = activeToDate;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "VolatilityDay [id=" + id + ", dayDate=" + dayDate + ", actionDescription=" + actionDescription
				+ ", description=" + description + ", active=" + active + ", activeFromDate=" + activeFromDate
				+ ", activeToDate=" + activeToDate + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + "]";
	}
}
