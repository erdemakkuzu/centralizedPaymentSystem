package com.testinc.centralizedpaymentsystem.consumer;

import com.testinc.centralizedpaymentsystem.configuration.ConsumerConfiguration;
import com.testinc.centralizedpaymentsystem.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OfflinePaymentConsumer extends PaymentConsumer{

    ConsumerConfiguration consumerConfiguration;
    PaymentService paymentService;

    @Autowired
    public OfflinePaymentConsumer(ConsumerConfiguration consumerConfiguration,
                                 PaymentService paymentService) {
        this.consumerConfiguration = consumerConfiguration;
        this.paymentService=paymentService;
    }

    @Override
    public void runConsumer() {
        this.consumer = createConsumer(consumerConfiguration.getTopicNameForOfflinePayments(),
                consumerConfiguration.getKafkaProducerHost(),
                consumerConfiguration.getKafkaConsumerGroupName(),
                consumerConfiguration.getConsumerReadingTimeOut(),
                consumerConfiguration.getConsumerFetchSize());

        final ConsumerRecords<String, String> consumerRecords =
                consumer.poll(consumerConfiguration.getConsumerReadingTimeOut());

        paymentService.saveOfflinePayments(consumerRecords);

        consumer.commitAsync();

        consumer.close();
        System.out.println("DONE");
    }
}
