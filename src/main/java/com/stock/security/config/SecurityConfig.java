package com.stock.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.stock.security.config.jwt.JwtAccessTokenFilter;

import com.stock.security.config.jwt.JwtRefreshUserFilter;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.config.user.UserInfoManagerConfig;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.service.CustomLogoutHandler;

import com.stock.security.service.LogoutHandlerService;
import com.stock.security.util.CookieService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * See https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html
 *     https://docs.spring.io/spring-security/reference/6.3/migration/authorization.html
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
	private final CookieService cookieService;

	private final JwtAccessTokenFilter jwtAccessTokenFilter;
	
	@Autowired
    private final CustomLogoutHandler customLogoutHandler;
	
	
	/** 
	 * sign-in is because Spring Boot has its own login somewhere 
	 * @param httpSecurity
	 * @return
	 * @throws Exception
	 */
//	@Order(1)
//    @Bean
//    public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//        		.csrf(AbstractHttpConfigurer::disable)
//                .securityMatcher("/sign-in")
//                .authorizeHttpRequests(auth -> auth
//            		.requestMatchers("/sign-in").permitAll()
////            		.requestMatchers(HttpMethod.GET, "/login").permitAll()
//                )
//                .userDetailsService(userInfoManagerConfig)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .httpBasic(Customizer.withDefaults())
//                .httpBasic(httpBasic -> httpBasic
//                        .authenticationEntryPoint(noPopupBasicAuthenticationEntryPoint()) // suppress pop-up
//                )
//                .build();
//    }

	@Order(1)
	@Bean
	public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.securityMatcher("/sign-in")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/sign-in").permitAll()
				)
				.userDetailsService(userInfoManagerConfig)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(httpBasic -> httpBasic
						.authenticationEntryPoint(noPopupBasicAuthenticationEntryPoint()) // suppress pop-up
				)
				.build();
	}

//	@Order(1)
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//	    return httpSecurity
//	            .csrf(AbstractHttpConfigurer::disable)
//	            .authorizeHttpRequests(auth -> auth
//	                .requestMatchers("/sign-in").permitAll()
//	                .anyRequest().authenticated()
//	            )
//	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	            .httpBasic(httpBasic -> 
//	                httpBasic.authenticationEntryPoint(noPopupBasicAuthenticationEntryPoint())
//	            )
//	            .build();
//	}

	
	
	/**
	 * This class is to suppress  The authentication pop-up is caused by the response header WWW-Authenticate: Basic, 
	 * which is set by BasicAuthenticationEntryPoint.
 	 * @return
	 */
	@Bean
    public AuthenticationEntryPoint noPopupBasicAuthenticationEntryPoint() {
        return new NoPopupBasicAuthenticationEntryPoint();
    }

	
	@Order(2)
	@Bean
	public SecurityFilterChain registerSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/sign-up")
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth.requestMatchers("/sign-up").permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> {
					log.error("ERROR sign-up" + ex.toString());
					ex.authenticationEntryPoint((request, response, authException) -> response
							.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()));
				})
				.build();
	}


	@Order(3)
	@Bean
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
	    return http
	        .securityMatcher("/api/**")
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.GET, "/api/ca-buy-list", "/api/us-buy-list", "/api/portfolios/*", "/api/portfolio-trades/*").hasAnyAuthority("SCOPE_WRITE", "ROLE_USER")
	            .requestMatchers(HttpMethod.POST,  "/api/portfolios", "/api/portfolios/*", "/api/portfolio-trade/*").hasAnyAuthority("SCOPE_WRITE", "ROLE_USER")
	            .requestMatchers(HttpMethod.PUT, "/api/portfolios").hasAnyAuthority("SCOPE_WRITE", "ROLE_USER")
	            .requestMatchers(HttpMethod.DELETE, "/api/portfolios/*").hasAnyAuthority("SCOPE_WRITE", "ROLE_USER")
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
	        .exceptionHandling(ex -> {
	            ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
	            ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
	        })
	        .build();
	}


	@Bean
	@Order(4)
	public SecurityFilterChain refreshUserSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
	  		.securityMatcher("/free/refresh-user")
	        .authorizeHttpRequests(auth -> 	auth.requestMatchers("/free/refresh-user").permitAll())
	        .addFilterBefore(new JwtRefreshUserFilter(rsaKeyRecord, jwtTokenUtils), SecurityContextHolderFilter.class)
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	       .build();
	  }		  


		/**
		 * Logout security filter chain
		 */
	@Bean
	@Order(5)
	public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.securityMatcher(new AntPathRequestMatcher("/logout/**"))
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils),
						UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
					.logoutUrl("/logout")
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.deleteCookies("refresh_token", "Cookie2")
//	                .addLogoutHandler(logoutHandlerService)
					.logoutSuccessHandler((request, response, authentication) -> {
					response.setStatus(HttpServletResponse.SC_OK);
		}))
			.exceptionHandling(ex -> {
				log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}", ex);
				ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
				ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
			})
			.build();
		}


		@Order(6)
	    @Bean
	    public SecurityFilterChain allCookiesSecurityFilterChain(HttpSecurity http) throws Exception {
	        return http
	        	.securityMatcher("/all-cookies")
	            .csrf(AbstractHttpConfigurer::disable)
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/all-cookies").permitAll()
	            )
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .exceptionHandling(ex -> {
	            	log.error("ERROR all-cookies" + ex.toString());
	                ex.authenticationEntryPoint((request, response, authException) ->
	                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()));
	            })
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
}



//@Order(3)
//@Bean
//public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//  return http
//      .securityMatcher("/api/**") // Use `securityMatcher` for Spring Security 6.x
//      .csrf(AbstractHttpConfigurer::disable)
////      .authorizeHttpRequests(auth -> auth
////          .requestMatchers("/api/**").hasAuthority("SCOPE_READ")  //.hasAnyRole("USER", "USUSER", "CAUSER") // .authenticated()
////      )
//      .authorizeHttpRequests(auth -> {
//  			auth.requestMatchers(HttpMethod.GET, "/api/ca-buy-list","/api/us-buy-list", "/api/portfolios/*"); 
//  			auth.requestMatchers(HttpMethod.POST, "/api/portfolios/*"); 
//  			auth.requestMatchers(HttpMethod.PUT, "/api/portfolios/update"); 
//  			auth.requestMatchers(HttpMethod.DELETE, "/api/portfolios/*");
//  			auth.anyRequest().authenticated();
//      })
//      .exceptionHandling(ex -> {
//          log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to: {}", ex);
//          ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//          ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//      })
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .httpBasic(Customizer.withDefaults())
//      .build();
//}


//@Order(3)
//@Bean
//public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//    return http
//        .securityMatcher("/api/**") // Use `securityMatcher` for Spring Security 6.x
//        .csrf(AbstractHttpConfigurer::disable)
////        .authorizeHttpRequests(auth -> auth
////            .requestMatchers("/api/**").hasAuthority("SCOPE_READ")  //.hasAnyRole("USER", "USUSER", "CAUSER") // .authenticated()
////        )
//        .authorizeHttpRequests(auth -> {
//  			auth.requestMatchers(HttpMethod.GET, "/api/ca-buy-list","/api/us-buy-list", "/api/portfolios/*"); 
//  			auth.requestMatchers(HttpMethod.POST, "/api/portfolios/*"); 
//  			auth.requestMatchers(HttpMethod.PUT, "/api/portfolios/*"); 
//  			auth.requestMatchers(HttpMethod.DELETE, "/api/portfolios/*");
//  			auth.anyRequest().authenticated();
//        })
//        .exceptionHandling(ex -> {
//            log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to: {}", ex);
//            ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//            ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//        })
//        .httpBasic(Customizer.withDefaults())
//        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//        .build();
//}

//.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


///**
//* Logout security filter chain
//*/
//@Order(5)
//@Bean
//public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//   return httpSecurity
//           .securityMatcher(new AntPathRequestMatcher("/logout/**"))
//           .csrf(AbstractHttpConfigurer::disable)
//           .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//          
//           .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//           .logout(logout -> logout
//                   .logoutUrl("/logout")
//                   .addLogoutHandler(logoutHandlerService)
//                   
//           )
//           .exceptionHandling(ex -> {
//               log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
//               ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//               ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//           })
//           .build();
//}	  

////Original
//@Order(5)
//@Bean
//public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//  return httpSecurity
//  		.csrf(AbstractHttpConfigurer::disable)
//  		.securityMatcher("/refresh-token")
//          .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//          .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////          .addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord,jwtTokenUtils,refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//          .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord,jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//          .exceptionHandling(ex -> {
//        	  ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//        	  ex.accessDeniedHandler((request, response, accessDeniedException) -> {
//        	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        	        response.getWriter().write("Access Denied: " + accessDeniedException.getMessage());
//        	    });
//          })
//          .httpBasic(withDefaults())
//          .build();
//}



//@Order(3)
//@Bean
//public SecurityFilterChain registerSecurityFilterChain(HttpSecurity http) throws Exception {
//	  log.info("/n/n @Order(3) ");
//    return http
//            .securityMatcher("/sign-up/**", "/all-cookies")
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .build();
//}


//@Order(4)
//@Bean
//public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception{
//	  log.info("/n/n @Order(4) ");
//  return http
//          .csrf(AbstractHttpConfigurer::disable)
//          .securityMatcher("/api/**")
//          .authorizeHttpRequests(auth -> {
//  			auth.requestMatchers(HttpMethod.GET, "/api/ca-buy-list","/api/us-buy-list"); 
//  			auth.anyRequest().authenticated();
//  		})
//          .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//          .exceptionHandling(ex -> {
//              log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
//              ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//              ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//          })
//          .httpBasic(Customizer.withDefaults())
//          .build();
//}	  	


//.logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
//.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
//.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))    


//@Bean
//public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity http) throws Exception {
//  return http
//      .securityMatcher("/logout")  // Applies to logout requests
//      .csrf(AbstractHttpConfigurer::disable)
//      .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//      .logout(logout -> logout
//          .addLogoutHandler(customLogoutHandler)
//          .logoutUrl("/logout")
//          .invalidateHttpSession(true)
//          .logoutSuccessHandler((request, response, authentication) -> {
//              log.info("Logout successful. Clearing security context.");
//              SecurityContextHolder.clearContext();
//          })
//      )
//      .exceptionHandling(ex -> {
//          log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to: {}", ex);
//          ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//          ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//      })
//      .build();
//}


//@Order(5)
//@Bean
//public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//  return httpSecurity
//  		.securityMatcher("/refresh-token")
//  		.csrf(AbstractHttpConfigurer::disable)
//  		
//  		.authorizeHttpRequests(auth -> auth
//  	              .requestMatchers("/refresh-token").permitAll()  // Allow access to sign-up
//  	              .anyRequest().authenticated()  // Other requests need to be authenticated
//  	          )
//  		.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
////          .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//          .addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord, jwtTokenUtils, refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//  		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .exceptionHandling(ex -> {
//              log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
//              ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//              ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//          })
//          .httpBasic(withDefaults())
//          .build();
//}


//@Order(6) before Customizer
//@Bean
//public SecurityFilterChain logoutSecurityFilterChainOrig(HttpSecurity http) throws Exception {
//	log.info("/n/n @Order(6) ");
//	return http
//            .securityMatcher("/logout/**")
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//            .logout(logout -> logout
//                    .logoutUrl("/logout")
//                    .addLogoutHandler(logoutHandlerService)
//                    .invalidateHttpSession(true)
//                    .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
////                    .deleteCookies("refresh_token","Cookie2")
//            )
//            .exceptionHandling(ex -> {
//                log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
//                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//            })
//            .build();
//}		

//@Order(7)
//@Bean
//public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
//	  return http
//		.csrf(AbstractHttpConfigurer::disable)
//		.securityMatcher("/free/**")
//      .authorizeHttpRequests((auth) -> {
//        	auth.requestMatchers("/free/*").permitAll();
//		})
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//     .build();
//}	


//Working copy
//@Order(2)
//@Bean
//public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//  return httpSecurity
//          .securityMatcher(new AntPathRequestMatcher("/api/**"))
//          .csrf(AbstractHttpConfigurer::disable)
//          .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .exceptionHandling(ex -> {
//              log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
//              ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//              ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//          })
//          .httpBasic(Customizer.withDefaults())
//			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//          .build();
//}



//Original
//@Order(2)
//@Bean
//public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//  return httpSecurity
//  		.csrf(AbstractHttpConfigurer::disable)
//  		.securityMatcher("/refresh-token/**")
//          .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//          .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord,jwtTokenUtils,refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//          .exceptionHandling(ex -> {
//              log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
//              ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//              ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//          })
//          .httpBasic(withDefaults())
//          .build();
//}


//"/refresh-token/**", ORIG
//@Order(7)
//@Bean
//public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
//    return httpSecurity
//    		.csrf(AbstractHttpConfigurer::disable)
//    		.securityMatcher("/log-out/**")
//            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            //.addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord,jwtTokenUtils,refreshTokenRepo), UsernamePasswordAuthenticationFilter.class)
//            .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
//            .exceptionHandling(ex -> {
//                log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
//                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//            })
//            .httpBasic(withDefaults())
//            .build();
//}	  	  

//@Bean
//public SecurityFilterChain api0SecurityFilterChain(HttpSecurity http) throws Exception{
//    return http
//              .securityMatcher(new AntPathRequestMatcher("/api/**"))
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .exceptionHandling(ex -> {
//                log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}",ex);
//                ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
//                ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//            })
//            .httpBasic(Customizer.withDefaults())
//				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//            .build();
//}	  


//@Order(2)
//@Bean
//public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//  return httpSecurity
////  		.csrf(AbstractHttpConfigurer::disable)
////          .securityMatcher("/login/**")
//          .securityMatcher("/login")
////          .securityMatcher(new AntPathRequestMatcher("/login"))
//          .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
//          .userDetailsService(userInfoManagerConfig)
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .exceptionHandling(ex -> {
//          	log.error("ERROR LOGIN" + ex.toString());
//              ex.authenticationEntryPoint((request, response, authException) ->
//                      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()));
//          })
//          .httpBasic(Customizer.withDefaults())
//          .build();
//}

//@Order(2)
//@Bean
//public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//  return httpSecurity
//          .securityMatcher("/login")
//          .authorizeHttpRequests(auth -> auth
////          		.requestMatchers("/login").permitAll()
//          		.requestMatchers(HttpMethod.GET, "/login").permitAll()
//          )
//          .userDetailsService(userInfoManagerConfig)
//          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//          .httpBasic(withDefaults())
//          .build();
//}


//@Order(3)
//@Bean
//public SecurityFilterChain registerSecurityFilterChain(HttpSecurity http) throws Exception {
//  return http
//  	.securityMatcher("/sign-up")
//      .csrf(AbstractHttpConfigurer::disable)
//      .authorizeHttpRequests(auth -> auth
//          .requestMatchers("/sign-up").permitAll()
//      )
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .exceptionHandling(ex -> {
//      	log.error("ERROR sign-up" + ex.toString());
//          ex.authenticationEntryPoint((request, response, authException) ->
//                  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()));
//      })
//      .build();
//}


//@Order(8)
//@Bean
//public SecurityFilterChain publicSecurityFilterChain1(HttpSecurity http) throws Exception {
//	  return http
//		.csrf(AbstractHttpConfigurer::disable)
//		.securityMatcher("/free/refresh-user")
//      .authorizeHttpRequests(auth -> 	auth.requestMatchers("/free/refresh-user").permitAll())
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//     .build();
//}		