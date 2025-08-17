package com.stock.security.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	
	@Value("${spring.security.cors}")
	private String cors;

	@Bean
	 public CorsFilter corsFilter() {

       CorsConfiguration config = new CorsConfiguration();
       
//       config.setAllowedOrigins(Arrays.asList("https://localhost:5004","http://localhost:5004","http://localhost:8440","http://localhost:9080","https://localhost:9443"));
       
       config.addAllowedOriginPattern("*");
       config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
       config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
       config.setAllowCredentials(true);
       config.setExposedHeaders(Arrays.asList("Authorization"));
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", config);
       
       return new CorsFilter(source);
   }
	
	
//	@Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        
//        config.setAllowedOrigins(List.of("https://localhost:5004"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Access-Control-Allow-Headers", "Access-Control-Expose-Headers"));
//        config.setAllowCredentials(true);
//        config.setExposedHeaders(List.of("Authorization"));
//        config.addAllowedMethod("*");
//        config.setMaxAge(3600L);
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
	
//	@Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        
//        CorsConfiguration config = new CorsConfiguration();
//        
//        config.setAllowedOrigins(Arrays.asList("https://localhost:5003"));
//        
//        config.setAllowCredentials(true);
////        config.addAllowedOriginPattern(this.cors);  //"http://localhost:5003"
////        config.addAllowedOrigin("https://localhost");
////        config.addAllowedOrigin("*");
//        config.addAllowedOrigin(this.cors);
//        
//        config.addAllowedOriginPattern("https://localhost:5003");
////        config.addAllowedOriginPattern("*");
//        config.addAllowedHeader("*");
////        config.addAllowedHeader("Access-Control-Allow-Headers","content-type,authorization");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }	
	
}
