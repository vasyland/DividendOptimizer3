package com.stock.security.config.jwt;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.stock.security.config.RSAKeyRecord;
import com.stock.security.entity.UserSubscription;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.util.CookieService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtRefreshUserFilter extends OncePerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtRefreshUserFilter.class);
	
	private final RSAKeyRecord rsaKeyRecord;
	private final JwtTokenUtils jwtTokenUtils;
	
	public JwtRefreshUserFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils) {
		super();
		this.rsaKeyRecord = rsaKeyRecord;
		this.jwtTokenUtils = jwtTokenUtils;
	}


	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		
		/* Find current refresh token in cookies */
		String token = getRefreshTokenFromCookies(request);

		log.info("REFRESH TOKEN FOUND: " + token);

		if (token == null || token.isEmpty()) {
			log.warn("[JwtRefreshUserFilter] No refresh token found in cookies");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\": \"Missing Refresh Token\", \"message\": \"Please log in again.\"}");
			response.getWriter().flush();
			return;
		}
		
		try {

			JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
			Jwt jwtRefreshToken = jwtDecoder.decode(token);

			// Check if refresh token is expired
	        Instant expirationTime = jwtRefreshToken.getExpiresAt();
	        if (expirationTime != null && expirationTime.isBefore(Instant.now())) {
//	        if (expirationTime != null && expirationTime.isAfter(Instant.now())) {
	            log.warn("[JwtRefreshUserFilter] Refresh token expired");

	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \"Refresh Token Expired\", \"message\": \"Please log in again.\"}");
	            response.getWriter().flush();
	            return;
	        }
			
			
			String userName = jwtTokenUtils.getUserName(jwtRefreshToken);
			log.info("[JwtRefreshUserFilter] Authenticated user: {}", userName);

			
			UserSubscription subscription = jwtTokenUtils.getUserSubscriptionsByEmail(userName).get(0);
			log.info("User Subscription Date: " + subscription.getSubscriptionExpiry());
			
//			LocalDate subscriptionExpiry = subscription.getSubscriptionExpiry();

			if (subscription.getSubscriptionExpiry().isBefore(LocalDate.now())) {
				log.warn("[JwtRefreshUserFilter] Subscription expired for user: {}", userName);

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType("application/json");
				response.getWriter().write(
						"{\"error\": \"Subscription Expired\", \"message\": \"Your subscription has expired. Please renew to continue.\"}");
				response.getWriter().flush();
				return;
			}

			// Retrieve user details (without password)
			UserDetails userDetails = jwtTokenUtils.userDetails(userName);

			// Create authentication token (without password)
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
					null, userDetails.getAuthorities());

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// Set authentication in SecurityContext
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication(authentication);
			SecurityContextHolder.setContext(securityContext);

		} catch (JwtValidationException e) {
			log.error("[JwtRefreshUserFilter:doFilterInternal] Invalid JWT: {}", e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Invalid Token\", \"message\": \"" + e.getMessage() + "\"}");
			response.getWriter().flush();
			return;

		} catch (JwtException e) { // Catches invalid signature and other JWT-related issues
			log.error("[JwtRefreshUserFilter:doFilterInternal] Invalid or tampered refresh token: {}", e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter()
					.write("{\"error\": \"Invalid Token\", \"message\": \"Refresh token verification failed.\"}");
			response.getWriter().flush();
			return;

		} catch (Exception e) {
			log.error("[JwtRefreshUserFilter:doFilterInternal] Unexpected error: {}", e.getMessage());

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Server Error\", \"message\": \"An unexpected error occurred.\"}");
			response.getWriter().flush();
			return;
		}
		
		log.info("[JwtRefreshTokenFilter:doFilterInternal] Completed");
        filterChain.doFilter(request, response);
	}
	
	
	private String getRefreshTokenFromCookies(HttpServletRequest request) {
	    if (request.getCookies() == null) {
	        return null;
	    }
	    
	    for (Cookie cookie : request.getCookies()) {
	        if ("refresh_token".equals(cookie.getName())) {
	            return cookie.getValue();
	        }
	    }
	    
	    return null;
	}

}
