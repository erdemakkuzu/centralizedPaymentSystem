

package com.testinc.centralizedpaymentsystem.service.impl;

import com.testinc.centralizedpaymentsystem.constans.PaymentError;
import com.testinc.centralizedpaymentsystem.entity.Accounts;
import com.testinc.centralizedpaymentsystem.entity.LogHistory;
import com.testinc.centralizedpaymentsystem.entity.Payments;
import com.testinc.centralizedpaymentsystem.repository.AccountsRepository;
import com.testinc.centralizedpaymentsystem.repository.LogHistoryRepository;
import com.testinc.centralizedpaymentsystem.repository.PaymentsRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    PaymentsRepository paymentsRepositoryMock = mock(PaymentsRepository.class);
    AccountsRepository accountsRepositoryMock = mock(AccountsRepository.class);
    LogHistoryRepository logHistoryRepositoryMock = mock(LogHistoryRepository.class);
    RestTemplate restTemplateMock = mock(RestTemplate.class);
    TopicPartition topicPartition = new TopicPartition("offline", 1);



    @BeforeEach
    void setUp() {

    }


    @Test
    void saveUnProcessedOnlinePayments_logErrorToDatabase_accountNotFound() {
        String CONSUMER_RECORD_ACCOUNT_NOT_FOUND = "{\"payment_id\": \"8e47aeab-0733-4142-a1ae-50db1a578518\"," +
                " \"account_id\": 1130, " +
                "\"payment_type\": \"offline\", " +
                "\"credit_card\": \"\", " +
                "\"amount\": 42, " +
                "\"delay\": 70}";

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "offline",
                CONSUMER_RECORD_ACCOUNT_NOT_FOUND);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);
        PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(paymentsRepositoryMock,
                accountsRepositoryMock,
                logHistoryRepositoryMock,
                restTemplateMock);
        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);

        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);

        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(),(PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription()));

    }

    @Test
    void saveUnProcessedOnlinePayments_logErrorToDatabase_paymentIdIsNotUnique() {
        String CONSUMER_RECORD_ACCOUNT_NOT_FOUND = "{\"payment_id\": \"8e47aeab-0733-4142-a1ae-50db1a578518\"," +
                " \"account_id\": 1130, " +
                "\"payment_type\": \"offline\", " +
                "\"credit_card\": \"\", " +
                "\"amount\": 42, " +
                "\"delay\": 70}";

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "offline",
                CONSUMER_RECORD_ACCOUNT_NOT_FOUND);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);
        PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(paymentsRepositoryMock,
                accountsRepositoryMock,
                logHistoryRepositoryMock,
                restTemplateMock);


        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);
        Payments mockPayments = mock(Payments.class);
        when(accountsRepositoryMock.findById(anyInt())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(anyString())).thenReturn(Optional.of(mockPayments));
        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);
        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(),(PaymentError.PAYMENT_ID_IS_NOT_UNIQUE.getErrorDescription()));

    }

    @Test
    void saveUnProcessedOnlinePayments_saveOnlinePayments_success() {
        String CONSUMER_RECORD_ACCOUNT_NOT_FOUND = "{\"payment_id\": \"8e47aeab-0733-4142-a1ae-50db1a578518\"," +
                " \"account_id\": 1130, " +
                "\"payment_type\": \"offline\", " +
                "\"credit_card\": \"\", " +
                "\"amount\": 42, " +
                "\"delay\": 70}";

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "offline",
                CONSUMER_RECORD_ACCOUNT_NOT_FOUND);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);
        PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(paymentsRepositoryMock,
                accountsRepositoryMock,
                logHistoryRepositoryMock,
                restTemplateMock);

        when(accountsRepositoryMock.findById(anyInt())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(anyString())).thenReturn(Optional.empty());
        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);
        verify(paymentsRepositoryMock, times(1)).save(any());


    }






}

