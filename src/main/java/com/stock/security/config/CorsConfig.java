package com.stock.security.config;

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
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
//        config.addAllowedOriginPattern(this.cors);  //"http://localhost:5003"
//        config.addAllowedOrigin("https://localhost");
//        config.addAllowedOrigin("*");
        config.addAllowedOrigin(this.cors);
        
        config.addAllowedOriginPattern("https://localhost:5003");
//        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
//        config.addAllowedHeader("Access-Control-Allow-Headers","content-type,authorization");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
