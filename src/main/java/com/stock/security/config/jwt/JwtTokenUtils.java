package com.stock.security.config.jwt;

import java.time.Instant;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.stock.security.config.user.UserInfoConfig;
import com.stock.security.repo.UserInfoRepo;

import lombok.RequiredArgsConstructor;

/**
 * @author atquil
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

	private final UserInfoRepo userInfoRepo;
	
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
    	
        final String userName = getUserName(jwtToken);
        boolean isTokenExpired = getIfTokenIsExpired(jwtToken);
        boolean isTokenUserSameAsDatabase = userName.equals(userDetails.getUsername());
        
        return !isTokenExpired  && isTokenUserSameAsDatabase;
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
}

