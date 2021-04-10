package com.testinc.centralizedpaymentsystem;

import com.testinc.centralizedpaymentsystem.consumer.KafkaConsumerExample;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CentralizedPaymentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentralizedPaymentSystemApplication.class, args);
	}

}
