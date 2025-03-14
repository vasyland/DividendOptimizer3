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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.stock.security.config.RSAKeyRecord;
import com.stock.security.dto.TokenType;
import com.stock.security.repo.RefreshTokenRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Thus filter is required during logout process using access token, but access token can be expired
 * We need to validate access token and regardless it is expired or not, and then to validate
 * 
 */
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutFilter extends OncePerRequestFilter {

	private  final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepo refreshTokenRepo;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		
		
		log.info("\n\n\n >>>>>>>>>>>>>>> $1000 JwtLogoutFilter Started");
		
		String rToken = null;
        
        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("refresh_token")){
                	rToken = cookie.getValue();
                	log.info("800 REFRESH TOKEN = " + rToken);
                }
            }
        }
        
        try{
        	
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");
            log.info("[JwtAccessTokenFilter:doFilterInternal] Filtering the Http Request:{}",request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("================== #400 authHeader: [" + authHeader +"]");

            JwtDecoder jwtDecoder =  NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();

            if(!authHeader.startsWith(TokenType.Bearer.name())){
            	
            	log.info("================== #401 authHeader doesn't start with Bearer");
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7); // Skip  the Bearer word

            final Jwt jwtToken = jwtDecoder.decode(token);
            log.info("================= #402 Jwt jwtToken from Bearer = " + jwtToken.getTokenValue());

            final String userName = jwtTokenUtils.getUserName(jwtToken);
            log.info("================= #403 userName = " + userName);
            
            if(!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
            	
            	log.info("==================     #404 getAuthentication() == null");
            	UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                
            	//if(jwtTokenUtils.isTokenValid(jwtToken, userDetails)){
                if(jwtTokenUtils.isTokenLegit(jwtToken, userDetails)){
                	
                	log.info("================== #405 isTokenLegit() ==============================");
                	
                	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                	
                	UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                	log.info("================== #406 createdToken = " + createdToken);
                	
                	/* Continue with our request */
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                    log.info("==================  #407 createdToken = " + createdToken);
                }
            }
            log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");

            log.info("#408 JwtLogoutFilter  response.getStatus() = " + response.getStatus());
            
                
            filterChain.doFilter(request, response);
        	
        }catch (JwtValidationException jwtValidationException){
            log.error("[JwtAccessTokenFilter:doFilterInternal] Exception due to :{}",jwtValidationException.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,jwtValidationException.getMessage());
        }
	}
}
