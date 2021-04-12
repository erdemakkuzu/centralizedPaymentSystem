

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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
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

    String CONSUMER_RECORD = "{\"payment_id\": \"8e47aeab-0733-4142-a1ae-50db1a578518\"," +
            " \"account_id\": 1130, " +
            "\"payment_type\": \"offline\", " +
            "\"credit_card\": \"\", " +
            "\"amount\": 42, " +
            "\"delay\": 70}";

    PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(paymentsRepositoryMock,
            accountsRepositoryMock,
            logHistoryRepositoryMock,
            restTemplateMock);

    @Test
    void saveUnProcessedOnlinePayments_logErrorToDatabase_accountNotFound() {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "online",
                CONSUMER_RECORD);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);

        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);

        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);

        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(), (PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription()));
    }

    @Test
    void saveUnProcessedOnlinePayments_logErrorToDatabase_paymentIdIsNotUnique() {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "online",
                CONSUMER_RECORD);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);

        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);
        Payments mockPayments = mock(Payments.class);
        when(accountsRepositoryMock.findById(anyInt())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(anyString())).thenReturn(Optional.of(mockPayments));
        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);
        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(), (PaymentError.PAYMENT_ID_IS_NOT_UNIQUE.getErrorDescription()));
    }

    @Test
    void saveUnProcessedOnlinePayments_saveOnlinePayments_success() {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "online",
                CONSUMER_RECORD);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);

        when(accountsRepositoryMock.findById(any())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(anyString())).thenReturn(Optional.empty());
        paymentServiceImpl.saveUnProcessedOnlinePayments(consumerRecords);
        verify(paymentsRepositoryMock, times(1)).save(any());
    }

    @Test
    void saveOfflinePayments_logErrorToDatabase_accountNotFound() {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "offline",
                CONSUMER_RECORD);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);

        paymentServiceImpl.saveOfflinePayments(consumerRecords);
        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);

        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(), (PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription()));
    }

    @Test
    void saveOfflinePayments_saveOfflinePayment_success() {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<String, String>("dummy",
                0, 1,
                "offline",
                CONSUMER_RECORD);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();
        consumerRecordList.add(consumerRecord);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListMap = new HashMap<>();
        topicPartitionListMap.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords = new ConsumerRecords(topicPartitionListMap);

        when(accountsRepositoryMock.findById(anyInt())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(anyString())).thenReturn(Optional.empty());
        paymentServiceImpl.saveOfflinePayments(consumerRecords);
        verify(paymentsRepositoryMock, times(1)).save(any());
    }

    @Test
    void processOnlinePayments_UnsuccessfulAPIResponse_logError() {
        List<Payments> paymentsList = new ArrayList<>();

        Payments payments1 = new Payments();
        payments1.setPaymentId("dummyId");
        payments1.setProcessed(false);
        payments1.setPaymentType("online");
        payments1.setValid(false);
        payments1.setAccounts(mock(Accounts.class));
        payments1.setAmount(100);
        payments1.setCreditCard("d-u-m-m-y");
        payments1.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        ResponseEntity<String> mockedResponseEntity = mock(ResponseEntity.class);
        HttpStatus mockedHttpStatus = HttpStatus.valueOf(404);

        paymentsList.add(payments1);

        when(paymentsRepositoryMock.findByProcessed(any(), any())).thenReturn(paymentsList);
        when(restTemplateMock.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>("dummy", HttpStatus.BAD_GATEWAY));
        when(mockedResponseEntity.getStatusCode()).thenReturn(mockedHttpStatus);
        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);

        ReflectionTestUtils.setField(paymentServiceImpl, "onlinePaymentProcessSize", 10);
        ReflectionTestUtils.setField(paymentServiceImpl, "paymentApiGateWayURL", "dummyrl");

        paymentServiceImpl.processOnlinePayments();

        verify(logHistoryRepositoryMock).save(argument.capture());
        assertEquals(argument.getValue().getErrorDescription(), (PaymentError.UNSUCCESSFUL_HTTP_RESPONSE_FROM_API_GATEWAY.getErrorDescription()));
    }

    @Test
    void processOnlinePayments_success_paymentProcessed() {
        List<Payments> paymentsList = new ArrayList<>();

        Payments payments1 = new Payments();
        payments1.setPaymentId("dummyId");
        payments1.setProcessed(false);
        payments1.setPaymentType("online");
        payments1.setValid(false);
        payments1.setAccounts(mock(Accounts.class));
        payments1.setAmount(100);
        payments1.setCreditCard("d-u-m-m-y");
        payments1.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        ResponseEntity<String> mockedResponseEntity = mock(ResponseEntity.class);
        HttpStatus mockedHttpStatus = HttpStatus.valueOf(404);

        paymentsList.add(payments1);

        when(paymentsRepositoryMock.findByProcessed(any(), any())).thenReturn(paymentsList);
        when(restTemplateMock.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>("dummy", HttpStatus.OK));
        when(mockedResponseEntity.getStatusCode()).thenReturn(mockedHttpStatus);
        ArgumentCaptor<LogHistory> argument = ArgumentCaptor.forClass(LogHistory.class);

        ReflectionTestUtils.setField(paymentServiceImpl, "onlinePaymentProcessSize", 10);
        ReflectionTestUtils.setField(paymentServiceImpl, "paymentApiGateWayURL", "dummyrl");

        when(accountsRepositoryMock.findById(any())).thenReturn(Optional.of(mock(Accounts.class)));
        when(paymentsRepositoryMock.findById(any())).thenReturn(Optional.of(payments1));


        paymentServiceImpl.processOnlinePayments();

        verify(accountsRepositoryMock, times(1)).save(any());
        verify(paymentsRepositoryMock, times(1)).save(any());
    }
}

