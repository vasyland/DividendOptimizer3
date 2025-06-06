package com.stock.security.entity;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.stock.model.Portfolio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "USER_INFO")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserInfo {

	public UserInfo() {
		super();
	}

	public UserInfo(Long id, String userName, String emailId, String password, String mobileNumber, String roles,
			Set<UserSubscription> subscriptions, List<Portfolio> portfolios) {
		super();
		this.id = id;
		this.userName = userName;
		this.emailId = emailId;
		this.password = password;
		this.mobileNumber = mobileNumber;
		this.roles = roles;
		this.subscriptions = subscriptions;
		this.portfolios = portfolios;
	}

//	(strategy = GenerationType.AUTO)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(nullable = false, name = "EMAIL_ID", unique = true)
	private String emailId;

	@Column(nullable = false, name = "PASSWORD")
	private String password;

	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;

	@Column(nullable = false, name = "ROLES")
	private String roles;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<UserSubscription> subscriptions;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Portfolio> portfolios;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Set<UserSubscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<UserSubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public List<Portfolio> getPortfolios() {
		return portfolios;
	}

	public void setPortfolios(List<Portfolio> portfolios) {
		this.portfolios = portfolios;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", userName=" + userName + ", emailId=" + emailId + ", password=" + password
				+ ", mobileNumber=" + mobileNumber + ", roles=" + roles + ", subscriptions=" + subscriptions
				+ ", portfolios=" + portfolios + "]";
	}
	
	
}