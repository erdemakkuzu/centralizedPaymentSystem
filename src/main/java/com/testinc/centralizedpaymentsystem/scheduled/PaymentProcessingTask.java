package com.testinc.centralizedpaymentsystem.scheduled;

import com.testinc.centralizedpaymentsystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class PaymentProcessingTask {

    PaymentService paymentService;

    @Autowired
    PaymentProcessingTask(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    public void processOnlinePayments() {
        paymentService.processOnlinePayments();
    }

}
