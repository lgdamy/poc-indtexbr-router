package com.tcc.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableZuulProxy
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("com.tcc")
public class ApiGatewayApplication {
	public static void main(String[] args) {

		SpringApplication.run(ApiGatewayApplication.class, args);
		
	}

}
