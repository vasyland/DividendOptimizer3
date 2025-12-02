package com.stock.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.beans.Customizer;

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
import com.stock.security.config.jwt.JwtRefreshTokenFilter;
import com.stock.security.config.jwt.JwtRefreshUserFilter;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.config.user.UserInfoManagerConfig;
import com.stock.security.service.CustomLogoutHandler;
import com.stock.security.util.CookieService;
import com.stock.security.repo.RefreshTokenRepo;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * See https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html
 *     https://docs.spring.io/spring-security/reference/6.3/migration/authorization.html
 * 
 */

@Configuration
@CrossOrigin(origins = "*")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final UserInfoManagerConfig userInfoManagerConfig;
	private final RSAKeyRecord rsaKeyRecord;
	private final JwtTokenUtils jwtTokenUtils;

	
	private JwtAccessTokenFilter jwtAccessTokenFilter;

	public SecurityConfig(UserInfoManagerConfig userInfoManagerConfig, RSAKeyRecord rsaKeyRecord,
			JwtTokenUtils jwtTokenUtils, CookieService cookieService, JwtAccessTokenFilter jwtAccessTokenFilter,
			CustomLogoutHandler customLogoutHandler, RefreshTokenRepo refreshTokenRepo) {
		super();
		this.userInfoManagerConfig = userInfoManagerConfig;
		this.rsaKeyRecord = rsaKeyRecord;
		this.jwtTokenUtils = jwtTokenUtils;
		this.jwtAccessTokenFilter = jwtAccessTokenFilter;
		this.customLogoutHandler = customLogoutHandler;
	}

	@Autowired
    private final CustomLogoutHandler customLogoutHandler;
	
	
	/** 
	 * sign-in is because Spring Boot has its own login somewhere 
	 * @param httpSecurity
	 * @return
	 * @throws Exception
	 */
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
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // <--- important
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.GET, "/api/ca-buy-list", "/api/us-buy-list", "/api/portfolios/**", "/api/weather/**").hasAnyAuthority("ROLE_USER")
	            .requestMatchers(HttpMethod.POST,  "/api/portfolios", "/api/portfolios/**", "/api/portfolio-trade/**").hasAnyAuthority("ROLE_USER")
	            .requestMatchers(HttpMethod.PUT, "/api/portfolios").hasAnyAuthority("ROLE_USER")
	            .requestMatchers(HttpMethod.DELETE, "/api/portfolios/*").hasAnyAuthority("ROLE_USER")
	            .anyRequest().authenticated()
	        )
//	        .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
	        .addFilterBefore(jwtAccessTokenFilter, UsernamePasswordAuthenticationFilter.class)
	        .exceptionHandling(ex -> {
	            ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
	            ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
	        })
	        .build();
	}


	
	@Bean
	@Order(4)
	public SecurityFilterChain refreshUserSecurityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(AbstractHttpConfigurer::disable)
	        .securityMatcher("/refresh-token/**")
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.OPTIONS, "/refresh-token/**").permitAll() // allow preflight
	            .anyRequest().permitAll() // allow POST
	        )
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .cors(cors -> {}) // delegate to global CorsFilter
	        .addFilterBefore(
	            new JwtRefreshTokenFilter(rsaKeyRecord, jwtTokenUtils), // updated
	            SecurityContextHolderFilter.class
	        );
	    return http.build();
	}




	
	
//	@Bean
//	@Order(4)
//	public SecurityFilterChain refreshUserSecurityFilterChain(HttpSecurity http) throws Exception {
//	    return http
//	        .csrf(AbstractHttpConfigurer::disable)
//	        .securityMatcher("/refresh-token/**")
//	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers(HttpMethod.OPTIONS, "/refresh-token/**").permitAll()
//	            .requestMatchers("/refresh-token/**").permitAll()
//	        )
//	        .addFilterBefore(new JwtRefreshUserFilter(rsaKeyRecord, jwtTokenUtils), SecurityContextHolderFilter.class)
//	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	        .cors(cors -> {}) // delegate to global CorsFilter
//	        .build();
//	}
	
	
	
// Works with browser
//	@Bean
//	@Order(4)
//	public SecurityFilterChain refreshUserSecurityFilterChain(HttpSecurity http) throws Exception {
//		log.info(">>>#4 Refresh Token Security Filter Chain");
//		return http
//			.csrf(AbstractHttpConfigurer::disable)
//	  		.securityMatcher("/refresh-token")
//	        .authorizeHttpRequests(auth -> 	auth.requestMatchers("/refresh-token/**").permitAll())
//	        .addFilterBefore(new JwtRefreshUserFilter(rsaKeyRecord, jwtTokenUtils), SecurityContextHolderFilter.class)
//	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	       .build();
//	  }		  


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

		/** 
		 * Check JwtSecurity Filter to set free points
		 * @param http
		 * @return
		 * @throws Exception
		 */
//		@Order(7)
//		@Bean
//		public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
//			System.out.println(">>> Refresh Token Security Filter Chain");
//		    return http
//		        .csrf(AbstractHttpConfigurer::disable)
//		        .securityMatcher("/refresh-token")
//		        .authorizeHttpRequests(auth -> auth
//		            .requestMatchers(HttpMethod.GET, "/refresh-token").permitAll()
//		        )
//		        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//		        .build();
//		}

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
