package com.stock.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.UserSubscription;

import java.util.List;

@Repository
public interface UserSubscriptionsRepository extends JpaRepository<UserSubscription, Long> {
	
	List<UserSubscription> findByUserId(Long userId);

}
