package com.stock.security.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_SUBSCRIPTION")
public class UserSubscription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;
	
//	@Column(name="user_id", nullable=false)
//	private Long userId;
	
	@Column(name = "subscription_end_date")
	private LocalDate subscriptionEndDate;
	
	@Column(name = "created_on")
	@CreationTimestamp
	private LocalDateTime createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private LocalDateTime updatedOn;
}
