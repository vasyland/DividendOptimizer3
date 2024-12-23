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
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow the origin from which the request is coming
        config.setAllowedOrigins(Arrays.asList("http://localhost:5004","https://localhost:5004","http://localhost:5003","https://localhost:5003","http://localhost:8440","http://localhost:9080","https://localhost:9443"));

        // Allow specific HTTP methods (GET, POST, etc.)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        
        // Allow any headers necessary for your app (e.g., Content-Type, Authorization)
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        
        // Allow credentials (optional)
        config.setAllowCredentials(true);
        
        // Add exposed headers, if needed
        config.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Register the configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
	
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
