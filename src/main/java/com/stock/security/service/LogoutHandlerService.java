package com.stock.security.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.stock.security.dto.TokenType;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.util.CookieService;

import jakarta.servlet.http.Cookie;

//import com.atquil.dto.TokenType;
//import com.atquil.repo.RefreshTokenRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atquil
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RefreshTokenRepo refreshTokenRepo;
    private final CookieService cookieService;

    
    public void origLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    	log.info("LOGOUT Handler: ");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("LOGOUT Handler authHeader = " + authHeader);
        
        if(!authHeader.startsWith(TokenType.Bearer.name())){
            return;
        }

        final String refreshToken = authHeader.substring(7);

        var storedRefreshToken = refreshTokenRepo.findByRefreshToken(refreshToken)
                .map(token->{
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                    return token;
                })
                .orElse(null);
    }
    
    
    /**
     * https://dzone.com/articles/how-to-use-cookies-in-spring-boot
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        /** We need to get a refresh token from the request in order revoke it in the database */
        Cookie[] cookies = request.getCookies();
        
        String refreshToken = "";
        /* Show all cookies  */
        log.info("\n\n#800 Started LogoutHandlerService.logout()");
        
        log.info("# 801 Authentication data");
        if(authentication == null) {
        	log.info("#802 Authentication data is NULL");
        }
//        log.info("#802 Authentication data - authentication.isAuthenticated() " + authentication.isAuthenticated());
//        log.info("#802 Authentication data - authentication.getPrincipal() " + authentication.getPrincipal().toString());
        
        if (cookies != null) {
        	
        	String searchedToken = cookieService.findCookieByName(cookies, "refresh_token");
        	log.info("#803 Searched Token with name refresh_token = " + searchedToken);
        	
            System.out.println("--------------- SHOW ALL COOKIES -------------------------------------------------------");
            for(Cookie c : cookies) {
            	System.out.println(c.getName() + " = " + c.getValue());
            }
            System.out.println("--------------- END OF ALL COOKIES -------------------------------------------------------");
        	
        	
//        	allCookies = Arrays.stream(cookies)
//                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
        	
        	Optional<Cookie> el = Arrays.stream(cookies)
    				.filter(e -> e.getName().contains("refresh_token"))
    				.findAny();
        	
        	System.out.println("Refresh Token: " + el.get());
        	
        	if(el != null) {
        		refreshToken = el.get().getValue();
//    			int inx = token.indexOf("=");
//    			oldRefreshToken = token.substring(inx+1);
    			System.out.println("Token Value: " + refreshToken);
    		}
        	
        } else {
        	log.error("Cookies are NULL");
        }
        
//        if(!authHeader.startsWith(TokenType.Bearer.name())){
//            return;
//        }

        //final String refreshToken = authHeader.substring(7);

        var storedRefreshToken = refreshTokenRepo.findByRefreshToken(refreshToken)
                .map(token->{
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                    return token;
                })
                .orElse(null);
    }
}
