package com.stock.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.stock.repositories.UserSubscriptionRepository;
import com.stock.security.entity.UserInfo;
import com.stock.security.entity.UserSubscription;
import com.stock.security.repo.UserInfoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	private final UserSubscriptionRepository userSubscriptionRepository;
	private final UserInfoRepository userInfoRepository;
	
	
	
	public UserService(UserSubscriptionRepository userSubscriptionRepository, UserInfoRepository userInfoRepository) {
		super();
		this.userSubscriptionRepository = userSubscriptionRepository;
		this.userInfoRepository = userInfoRepository;
	}

	public Optional<UserInfo> getUserInfo(String email) {
		return userInfoRepository.findByEmailId(email);
	}
	
	public List<UserSubscription> getUserSubscriptionsByEmail(String email) {
		return userSubscriptionRepository.findByUserEmailId(email);
	}
	
	public Long getCurrentUserId() {
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    return userInfoRepository.findByEmailId(username)
	            .orElseThrow(() -> new RuntimeException("User not found"))
	            .getId();
	}
}
