package com.stock.services;

import org.springframework.stereotype.Service;
import com.stock.repositories.UserSubscriptionsRepository;
import com.stock.model.UserSubscription;
import java.util.List;

@Service
public class UserSubscripitonsService {

	private final UserSubscriptionsRepository repository;
	
	public UserSubscripitonsService(UserSubscriptionsRepository repository) {
        this.repository = repository;
    }
	
	// Method to get subscriptions by user ID
    public List<UserSubscription> getSubscriptionsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}
