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
import org.springframework.web.bind.annotation.CrossOrigin;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.stock.security.config.jwt.JwtAccessTokenFilter;
import com.stock.security.config.jwt.JwtLogoutFilter;
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
	
//	public SecurityConfig() {}
//	
//	public SecurityConfig(
//			UserInfoManagerConfig userInfoManagerConfig,
//			RSAKeyRecord rsaKeyRecord,
//			JwtTokenUtils jwtTokenUtils,
//			RefreshTokenRepo refreshTokenRepo,
//			LogoutHandlerService logoutHandlerService) {
//		
//		this.userInfoManagerConfig = userInfoManagerConfig;
//		this.rsaKeyRecord = rsaKeyRecord;
//		this.jwtTokenUtils = jwtTokenUtils;
//		this.refreshTokenRepo = refreshTokenRepo;
//		this.logoutHandlerService = logoutHandlerService;
//	}
	
    @Order(1)
    @Bean
    public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    	log.info("/n/n @Order(1) ");
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
	  log.info("/n/n @Order(2) ");
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
	  public SecurityFilterChain registerSecurityFilterChain(HttpSecurity http) throws Exception {
		  log.info("/n/n @Order(3) ");
	      return http
	              .securityMatcher("/sign-up/**", "/all-cookies")
	              .csrf(AbstractHttpConfigurer::disable)
	              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
	              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	              .build();
	  }
  
	  
	  @Order(4)
	  @Bean
	  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception{
		  log.info("/n/n @Order(4) ");
	    return http
	            .csrf(AbstractHttpConfigurer::disable)
	            .securityMatcher("/api/**")
	            .authorizeHttpRequests(auth -> {
	    			auth.requestMatchers(HttpMethod.GET, "/api/ca-buy-list","/api/us-buy-list"); 
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

	  
		@Order(6)
		@Bean
		public SecurityFilterChain logoutSecurityFilterChainOrig(HttpSecurity http) throws Exception {
			log.info("/n/n @Order(6) ");
			return http
		            .securityMatcher("/logout/**")
		            .csrf(AbstractHttpConfigurer::disable)
		            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
		            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
		            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
		            .logout(logout -> logout
		            		.addLogoutHandler(logoutHandlerService)
		            		.logoutUrl("/logout")
		                    .invalidateHttpSession(true)
		                    .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
//		                    .deleteCookies("refresh_token","Cookie2")
		            )
		            .exceptionHandling(ex -> {
		                log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
		                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
		                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
		            })
		            .build();
		}	  

		
//		@Order(6) before Customizer
//		@Bean
//		public SecurityFilterChain logoutSecurityFilterChainOrig(HttpSecurity http) throws Exception {
//			log.info("/n/n @Order(6) ");
//			return http
//		            .securityMatcher("/logout/**")
//		            .csrf(AbstractHttpConfigurer::disable)
//		            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//		            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//		            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//		            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//		            .logout(logout -> logout
//		                    .logoutUrl("/logout")
//		                    .addLogoutHandler(logoutHandlerService)
//		                    .invalidateHttpSession(true)
//		                    .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
////		                    .deleteCookies("refresh_token","Cookie2")
//		            )
//		            .exceptionHandling(ex -> {
//		                log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
//		                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//		                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//		            })
//		            .build();
//		}		
		
//	  @Order(5)
//	  @Bean
//	  public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//	      return httpSecurity
//	      		.csrf(AbstractHttpConfigurer::disable)
//	      		.securityMatcher("/log-out/**")
//	      		.authorizeHttpRequests(auth -> {
//	    			auth.requestMatchers(HttpMethod.POST, "/log-out"); 
//	    			auth.anyRequest().authenticated();
//	    		})
////	      		.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//	      		.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	              //.addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord, jwtTokenUtils, refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
////	              .addFilterBefore(new JwtLogoutFilter(rsaKeyRecord, jwtTokenUtils, refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//	            .addFilterBefore(new JwtLogoutFilter(rsaKeyRecord, jwtTokenUtils, refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//	              .exceptionHandling(ex -> {
//	                  log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Log out Exception due to :{}",ex);
//	                  ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//	                  ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//	              })
////	              .httpBasic(Customizer.withDefaults())
//	              .build();
//	  }	  
	  
  


		//"/refresh-token/**", ORIG
//	  @Order(7)
//	  @Bean
//	  public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//	      return httpSecurity
//	      		.csrf(AbstractHttpConfigurer::disable)
//	      		.securityMatcher("/log-out/**")
//	              .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//	              .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
//	              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	              //.addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord,jwtTokenUtils,refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//	              .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//	              .exceptionHandling(ex -> {
//	                  log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
//	                  ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//	                  ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//	              })
//	              .httpBasic(withDefaults())
//	              .build();
//	  }	  	  
	  
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
	  
	  
  @Order(7)
  @Bean
  public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
	  log.info("/n/n @Order(7) ");
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
