package com.stock.security.config.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtRefreshTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRefreshTokenFilter.class);

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtRefreshTokenFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils) {
        this.rsaKeyRecord = rsaKeyRecord;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String rToken = null;

        // Retrieve refresh token from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    rToken = cookie.getValue();
                    log.info("Found refresh token in cookie: {}", rToken);
                    break;
                }
            }
        }

        // If refresh token exists and user is not authenticated
        if (rToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
                Jwt jwtRefreshToken = jwtDecoder.decode(rToken);
                String userName = jwtTokenUtils.getUserName(jwtRefreshToken);

                if (userName != null && !userName.isEmpty()) {
                    UserDetails userDetails = jwtTokenUtils.userDetails(userName);

                    if (jwtTokenUtils.isTokenValid(jwtRefreshToken, userDetails)) {
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        securityContext.setAuthentication(authenticationToken);
                        SecurityContextHolder.setContext(securityContext);
                        log.info("Refresh token valid. Security context set for user: {}", userName);
                    }
                }
            } catch (JwtValidationException e) {
                log.error("Invalid refresh token: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            } catch (Exception e) {
                log.error("Error processing refresh token: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing refresh token");
            }
        } else {
            log.info("No refresh token cookie found or user already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
