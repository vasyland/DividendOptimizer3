package com.stock.security.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private static final Logger log = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		log.info("CUSTOM LOGOUT SUCCESS HANDLER 2: " + authentication.getName());
		
		// Log the logout event
        if (authentication != null) {
            String username = authentication.getName();
            System.out.println("User logged out: " + username);
        }

     // Clear security context (already handled by Spring Security)
        SecurityContextHolder.clearContext();
        
     // Return a success response
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"#6 Logout successful\"}");
        response.getWriter().flush();
	}

	
	private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
            	
            	log.info("COOKIE NAME: " + cookie.getName());
            	
                Cookie clearedCookie = new Cookie(cookie.getName(), "");
                clearedCookie.setMaxAge(0); // Set to expire immediately
                clearedCookie.setPath("/"); // Path must match the original cookie
                clearedCookie.setHttpOnly(true); // Secure cookie if applicable
                clearedCookie.setSecure(true); // Use true if using HTTPS
                response.addCookie(clearedCookie);
            }
        }
        
        
    }
}
