package com.testinc.centralizedpaymentsystem.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfiguration {

    @Value("${kafka.payments.online.topic-name}")
    private String topicNameForOnlinePayments;

    @Value("${kafka.payments.offline.topic-name}")
    private String topicNameForOfflinePayments;

    @Value("${kafka.host-address}")
    private String kafkaProducerHost;

    @Value("${kafka.consumer.group-name}")
    private String kafkaConsumerGroupName;

    @Value("${kafka.consumer.read-time-out}")
    private long consumerReadingTimeOut;

    @Value("${kafka.consumer.fetch-size}")
    private int consumerFetchSize;

    public int getConsumerFetchSize() {
        return consumerFetchSize;
    }

    public String getTopicNameForOnlinePayments() {
        return topicNameForOnlinePayments;
    }

    public String getTopicNameForOfflinePayments() {
        return topicNameForOfflinePayments;
    }

    public String getKafkaProducerHost() {
        return kafkaProducerHost;
    }

    public String getKafkaConsumerGroupName() {
        return kafkaConsumerGroupName;
    }

    public long getConsumerReadingTimeOut() {
        return consumerReadingTimeOut;
    }

}
