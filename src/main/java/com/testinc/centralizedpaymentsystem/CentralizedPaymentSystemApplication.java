package com.testinc.centralizedpaymentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class CentralizedPaymentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentralizedPaymentSystemApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		return builder
				/*.setConnectTimeout(Duration.ofMillis(3000))
				.setReadTimeout(Duration.ofMillis(3000))*/
				.build();
	}

}



