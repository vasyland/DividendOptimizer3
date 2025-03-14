package com.stock.security.config.jwt;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.stock.repositories.UserSubscriptionRepository;
import com.stock.security.config.user.UserInfoConfig;
import com.stock.security.entity.UserSubscription;
import com.stock.security.repo.UserInfoRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atquil
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {

	private final UserInfoRepo userInfoRepo;
	private final UserSubscriptionRepository userSubscriptionRepository;
	
    public String getUserName(Jwt jwtToken){
        return jwtToken.getSubject();
    }

    
    /**
     * Check if the Token valid
     * @param jwtToken
     * @param userDetails
     * @return
     */
    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){
    	
    	log.info("#600 jwtToken.getIssuedAt() = " + jwtToken.getIssuedAt());
    	
        final String userName = getUserName(jwtToken);
        boolean isTokenExpired = false;  // getIfTokenIsExpired(jwtToken);
        boolean isTokenUserSameAsDatabase = userName.equals(userDetails.getUsername());
        
        log.info("#601 userName = " + userName);
        log.info("#602 isTokenExpired = " + isTokenExpired);
        log.info("#603 isTokenUserSameAsDatabase = " + isTokenUserSameAsDatabase);
        
        return !isTokenExpired  && isTokenUserSameAsDatabase;
    }

    
    /**
     * Check if the Token valid
     * @param jwtToken
     * @param userDetails
     * @return
     */
    public boolean isTokenLegit(Jwt jwtToken, UserDetails userDetails){
    	
    	log.info("#700 jwtToken.isTokenLegit() = " + jwtToken.getIssuedAt());
    	
        final String userName = getUserName(jwtToken);
        boolean isTokenUserSameAsDatabase = userName.equals(userDetails.getUsername());
        
        log.info("#701 isTokenLegit -> userName = " + userName);
        log.info("#702 isTokenLegit -> isTokenUserSameAsDatabase = " + isTokenUserSameAsDatabase);
        
        return isTokenUserSameAsDatabase;
    }    
    
    
    /**
     * Check if the Token expired
     * @param jwtToken
     * @return
     */
    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    
    /**
     * Getting a user info from the database
     * @param email
     * @return
     */
    public UserDetails userDetails(String email){
        return userInfoRepo
                .findByEmailId(email)
                .map(UserInfoConfig::new)
                .orElseThrow(()-> new UsernameNotFoundException("UserEmail: "+email+" does not exist"));
    }
    
    
    /**
     * Getting all user subscriptions by User Id
     */
    public List<UserSubscription> getUserSubscriptionsByEmail(String email) {
        return userSubscriptionRepository.findByUserEmailId(email);
    }
}

