package com.stock.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.security.entity.UserSubscription;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {
	
	List<UserSubscription> findByUserEmailId(String emailId);

}
