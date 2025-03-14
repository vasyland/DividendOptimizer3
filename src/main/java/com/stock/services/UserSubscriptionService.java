package com.stock.services;

import org.springframework.stereotype.Service;
import com.stock.repositories.UserSubscriptionRepository;
import com.stock.security.entity.UserSubscription;

import java.util.List;

@Service
public class UserSubscriptionService {

	  private final UserSubscriptionRepository userSubscriptionRepository;

	    public UserSubscriptionService(UserSubscriptionRepository userSubscriptionRepository) {
	        this.userSubscriptionRepository = userSubscriptionRepository;
	    }

	    public List<UserSubscription> getUserSubscriptionsByEmail(String email) {
	        return userSubscriptionRepository.findByUserEmailId(email);
	    }
}
