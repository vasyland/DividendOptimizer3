package com.stock.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.stock.security.config.jwt.JwtAccessTokenFilter;
import com.stock.security.config.jwt.JwtRefreshTokenFilter;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.config.user.UserInfoManagerConfig;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.service.LogoutHandlerService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * See https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html
 * 
 */

@Configuration
@CrossOrigin(origins = "*")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

	private final UserInfoManagerConfig userInfoManagerConfig;
	private final RSAKeyRecord rsaKeyRecord;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenRepo refreshTokenRepo;
	private final LogoutHandlerService logoutHandlerService;
	
    @Order(1)
    @Bean
    public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
        		.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/login/**")
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                .userDetailsService(userInfoManagerConfig)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint((request, response, authException) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()));
                })
                .httpBasic(withDefaults())
                .build();
    }
	
  @Order(2)
  @Bean
  public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
      return httpSecurity
      		.csrf(AbstractHttpConfigurer::disable)
      		.securityMatcher("/refresh-token/**")
              .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
              .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord,jwtTokenUtils,refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
              .exceptionHandling(ex -> {
                  log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
                  ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
                  ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
              })
              .httpBasic(withDefaults())
              .build();
  }
	
 
	@Order(3)
	@Bean
	public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .securityMatcher("/logout/**")
	            .csrf(AbstractHttpConfigurer::disable)
	            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord,jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
	            .logout(logout -> logout
	                    .logoutUrl("/logout")
	                    .addLogoutHandler(logoutHandlerService)
	                    .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
	            )
	            .exceptionHandling(ex -> {
	                log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
	                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
	                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
	            })
	            .build();
	}
  
	  @Order(4)
	  @Bean
	  public SecurityFilterChain registerSecurityFilterChain(HttpSecurity http) throws Exception{
	      return http
	              .securityMatcher("/sign-up/**")
	              .csrf(AbstractHttpConfigurer::disable)
	              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
	              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	              .build();
	  }
  
	  
	  @Order(5)
	  @Bean
	  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception{
	    return http
	            .csrf(AbstractHttpConfigurer::disable)
	            .securityMatcher("/api/**")
	            .authorizeHttpRequests(auth -> {
	    			auth.requestMatchers(HttpMethod.GET, "/api/buy-list"); 
	    			auth.anyRequest().authenticated();
	    		})
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
	            .exceptionHandling(ex -> {
	                log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
	                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
	                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
	            })
	            .httpBasic(Customizer.withDefaults())
	            .build();
	}	  	
	  
	  
//	  @Bean
//	  public SecurityFilterChain api0SecurityFilterChain(HttpSecurity http) throws Exception{
//	      return http
//	                .securityMatcher(new AntPathRequestMatcher("/api/**"))
//	              .csrf(AbstractHttpConfigurer::disable)
//	              .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//	              .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
//	              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	              .exceptionHandling(ex -> {
//	                  log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
//	                  ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//	                  ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//	              })
//	              .httpBasic(Customizer.withDefaults())
//					.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//	              .build();
//	  }	  
	  
	  
  @Order(6)
  @Bean
  public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception{
      return http
  		.csrf(AbstractHttpConfigurer::disable)
  		.securityMatcher("/free/**")
        .authorizeHttpRequests((auth) -> {
          	auth.requestMatchers("/free/*").permitAll();
  		})
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
       .build();
  }	

  
  
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(){
        JWK jwk = new RSAKey.Builder(rsaKeyRecord.rsaPublicKey()).privateKey(rsaKeyRecord.rsaPrivateKey()).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

//    Working copy
//    @Order(2)
//    @Bean
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//        return httpSecurity
//                .securityMatcher(new AntPathRequestMatcher("/api/**"))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling(ex -> {
//                    log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
//                    ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//                    ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//                })
//                .httpBasic(Customizer.withDefaults())
//				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .build();
//    }
    
}
