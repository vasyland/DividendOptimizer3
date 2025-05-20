package com.stock.security.config.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.stock.security.config.RSAKeyRecord;
import com.stock.security.dto.TokenType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atquil
 * Custom JwtFilter to validate JWTs that are included in the Authorization header of HTTP Request.

UseCase: User is removed, then also jwtAccessToken will work, so prevent it. 

Let's create the Filter

    OncePerRequestFilter: The filter is implemented as a subclass of OncePerRequestFilter, which ensures that the filter is only applied once per request.
    The filter uses the rsaKeyRecord object to obtain the RSA public and private keys used to sign and verify the JWTs.
    JWT
        Valid: The filter creates an Authentication object and sets it in the SecurityContextHolder. The Authentication object contains the user details and authorities extracted from the JWT.
        In-valid: If the JWT is not valid, the filter throws a ResponseStatusException with an HTTP 406 Not Acceptable status code

 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	
    	log.info("[JwtAccessTokenFilter] #0 Request received: {}", request.getRequestURI());
    	log.info("[JwtAccessTokenFilter:doFilterInternal] :: #1 Started ");
    	
    	// Get request URI
        String requestURI = request.getRequestURI();
        
    	// Skip JWT filter for refresh token endpoint
        if (requestURI.equals("/sign-in") 
        		|| requestURI.equals("/free/refresh-user") 
        		|| requestURI.equals("/free/free-ca-buy-list")
        		|| requestURI.equals("/free/free-us-buy-list")
        		|| requestURI.equals("/free/all-companies")
        		|| requestURI.equals("/free/searchBySymbol")
        		|| requestURI.equals("/free/searchByName")
        		|| requestURI.equals("/sign-up") 
        		|| requestURI.equals("/all-cookies")) {
            filterChain.doFilter(request, response);
            return;
        }
    	
        try{
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: #2 Started ");
            log.info("[JwtAccessTokenFilter:doFilterInternal] #3 Filtering the Http Request:{}",request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            JwtDecoder jwtDecoder =  NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();

//            if(!authHeader.startsWith(TokenType.Bearer.name())){
//                filterChain.doFilter(request,response);
//                return;
//            }

            log.info("[JwtAccessTokenFilter:doFilterInternal] :: #4 authHeader = " + authHeader);
            
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                filterChain.doFilter(request, response); // Continue without authentication
//                return;
//            }
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("[JwtAccessTokenFilter] Missing or invalid Authorization header.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing or invalid token");
                return;
            }

            
            final String token = authHeader.substring(7);
            final Jwt jwtToken = jwtDecoder.decode(token);

            final String userName = jwtTokenUtils.getUserName(jwtToken);
            log.info("[JwtAccessTokenFilter.doFilterInternal] => token: " + token);

            if(!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){

                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                
                if(jwtTokenUtils.isTokenValid(jwtToken,userDetails)){
                
                	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");
            filterChain.doFilter(request,response);
            
        } catch (JwtValidationException jwtValidationException) {
            log.error("[JwtAccessTokenFilter:doFilterInternal] JWT Validation Failed: {}", jwtValidationException.getMessage());

            // Set response status and JSON response instead of throwing an exception
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT Expired\", \"message\": \"" + jwtValidationException.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }
}