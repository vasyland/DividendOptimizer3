package com.stock.security.util;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CookieService {

	
	/**
	 * Find a required cookie in the response
	 * @param cookies
	 * @param cookieName
	 * @return
	 */
	public String findCookieByName(Cookie[] cookies, String cookieName) {
		
		/* Show all cookies  */
        System.out.println("--------------- SHOW ALL COOKIES -------------------------------------------------------");
        for(Cookie c : cookies) {
        	System.out.println(c.getName() + " = " + c.getValue());
        }
        System.out.println("--------------- END OF ALL COOKIES -------------------------------------------------------");
		
        String searchedToken = "";
        if (cookies != null) {

        	Optional<Cookie> el = Arrays.stream(cookies)
    				.filter(e -> e.getName().contains(cookieName))
    				.findAny();
        	
        	if(el != null) {
        		searchedToken = el.get().getValue();
//    			log.info("Found Token Value: " + searchedToken);
    		}
        }
		return searchedToken;
	}
	
	
}
