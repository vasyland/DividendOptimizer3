package com.stock.security.service;

/**
 * @author atquil
 */
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenGenerator {

	private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);

    private final JwtEncoder jwtEncoder;

    /**
     * Generating Access Token
     * @param authentication
     * @return
     */
    public String generateAccessToken(Authentication authentication) {

        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());

        String roles = getRolesOfUser(authentication);
        log.info("#1 ROLES TP GENERATE TOKEN: " + roles);
        
        String permissions = getPermissionsFromRoles(roles);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("iwm3")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(3, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    /**
     * Generating Refresh Token available for 3 months
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication) {

        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("iwm3")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
    
    /**
     * Extracting roles from the authentication object
     * @param authentication
     * @return
     */
    private static String getRolesOfUser(Authentication authentication) {
    	
    	log.info("=========   USER AUTHORITIES  =================");
    	List<? extends GrantedAuthority> la = (List<? extends GrantedAuthority>) authentication.getAuthorities();
    	la.forEach(t -> {
    		log.info(t.getAuthority());
    	});
    	
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }


    /**
     * Permissions
     * @param roles
     * @return
     */
    private String getPermissionsFromRoles(String roles) {
        Set<String> permissions = new HashSet<>();

        if (roles.contains("ROLE_PAIDCA")) {
            permissions.addAll(List.of("READ", "WRITE", "DELETE", "MESSAGE"));
        }
        if (roles.contains("ROLE_PAIDUS")) {
            permissions.add("READ");
        }
        if (roles.contains("ROLE_USER")) {
            permissions.add("READ");
        }
        return String.join(" ", permissions);
    }

}
