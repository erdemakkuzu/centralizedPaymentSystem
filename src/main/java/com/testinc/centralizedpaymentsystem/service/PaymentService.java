package com.testinc.centralizedpaymentsystem.service;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    void saveUnProcessedOnlinePayments(ConsumerRecords<String, String> consumerRecords);

    void saveOfflinePayments(ConsumerRecords<String, String> consumerRecords);
}
