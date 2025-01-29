package com.stock;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import com.stock.security.config.RSAKeyRecord;


@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication(scanBasePackages = {"com.stock.", "com.stock.security"})
public class DividendOptimizer3Application {

	public static void main(String[] args) {
		SpringApplication.run(DividendOptimizer3Application.class, args);
	}
	
	private Connector connector() {
		Connector c = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		c.setPort(8080);
		c.setSecure(false);
		c.setScheme("http");
		return c;
	}
	
	@Bean
	public ServletWebServerFactory servletWebServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addAdditionalTomcatConnectors(connector());
		return factory;
	}

}
