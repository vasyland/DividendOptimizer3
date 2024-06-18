package com.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.stock.security.config.RSAKeyRecord;

@SpringBootApplication
@EnableConfigurationProperties(RSAKeyRecord.class)
public class DividendOptimizer3Application {

	public static void main(String[] args) {
		SpringApplication.run(DividendOptimizer3Application.class, args);
	}

}
