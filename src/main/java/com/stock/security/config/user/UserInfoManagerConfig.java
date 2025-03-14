package com.stock.security.config.user;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stock.security.entity.UserInfo;
import com.stock.security.repo.UserInfoRepo;

import lombok.RequiredArgsConstructor;

/**
 * Getting a User information in the form of an Authentication object
 * from the database or from other sources
 */
@Service
@RequiredArgsConstructor
public class UserInfoManagerConfig implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserInfoManagerConfig.class);
	
    private final UserInfoRepo userInfoRepo;
    
      
	@Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
		
		UserInfo u0 = userInfoRepo.findByEmailId(emailId).get();
		
		log.info("#5 EMAIL FROM DB: " + u0.getEmailId());
		
		UserDetails ud = userInfoRepo
                .findByEmailId(emailId)  // Optional<UserInfoEntity>
                .map(UserInfoConfig::new) // Optional<UserInfoConfig>
                .orElseThrow(()-> new UsernameNotFoundException("UserInfoManagerConfig ERROR: User Email: " + emailId + " does not exist"));
		
        return ud;
    }
}
