package com.testinc.centralizedpaymentsystem.scheduled;

import com.testinc.centralizedpaymentsystem.consumer.OfflinePaymentConsumer;
import com.testinc.centralizedpaymentsystem.consumer.OnlinePaymentConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ConsumerTasks {

    OnlinePaymentConsumer onlinePaymentConsumer;

    OfflinePaymentConsumer offlinePaymentConsumer;

    public ConsumerTasks(OnlinePaymentConsumer onlinePaymentConsumer,
                         OfflinePaymentConsumer offlinePaymentConsumer) {
        this.offlinePaymentConsumer = offlinePaymentConsumer;
        this.onlinePaymentConsumer = onlinePaymentConsumer;

    }

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void consumeOnlinePayments() {
        onlinePaymentConsumer.runConsumer();
    }

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void consumeOfflinePayments() {
        offlinePaymentConsumer.runConsumer();
    }

}
