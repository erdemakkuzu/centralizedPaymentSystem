package com.testinc.centralizedpaymentsystem.scheduled;

import com.testinc.centralizedpaymentsystem.configuration.ConsumerConfiguration;
import com.testinc.centralizedpaymentsystem.consumer.OfflinePaymentConsumer;
import com.testinc.centralizedpaymentsystem.consumer.OnlinePaymentConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class PaymentProcessingTask {

    @Autowired
    OnlinePaymentConsumer onlinePaymentConsumer;

    @Autowired
    OfflinePaymentConsumer offlinePaymentConsumer;

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void processOnlinePayments() {
        onlinePaymentConsumer.runConsumer();
    }

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void processOfflinePayments() {
        offlinePaymentConsumer.runConsumer();
    }
}
