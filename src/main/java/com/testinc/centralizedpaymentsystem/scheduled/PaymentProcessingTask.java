package com.testinc.centralizedpaymentsystem.scheduled;

import com.testinc.centralizedpaymentsystem.configuration.ConsumerConfiguration;
import com.testinc.centralizedpaymentsystem.consumer.PaymentConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class PaymentProcessingTask {

    @Autowired
    ConsumerConfiguration consumerConfiguration;

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void processOnlinePayments() {
        PaymentConsumer onlinePaymentConsumer = new PaymentConsumer(
                consumerConfiguration.getTopicNameForOnlinePayments(),
                consumerConfiguration.getKafkaProducerHost(),
                consumerConfiguration.getKafkaConsumerGroupName(),
                consumerConfiguration.getConsumerReadingTimeOut(),
                consumerConfiguration.getConsumerFetchSize());

        onlinePaymentConsumer.runConsumer();
    }

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void processOfflinePayments() {
        System.out.println(Thread.currentThread().getName() + " Task 2 executed at " + new Date());
    }
}
