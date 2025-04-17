package com.stock.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.UserSubscriptionRepository;
import com.stock.security.entity.UserInfo;
import com.stock.security.entity.UserSubscription;
import com.stock.security.repo.UserInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserSubscriptionRepository userSubscriptionRepository;
	private final UserInfoRepository userInfoRepository;
	
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
