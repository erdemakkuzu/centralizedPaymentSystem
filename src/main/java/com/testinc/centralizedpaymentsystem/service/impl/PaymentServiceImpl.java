package com.testinc.centralizedpaymentsystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testinc.centralizedpaymentsystem.dto.PaymentDTO;
import com.testinc.centralizedpaymentsystem.entity.Accounts;
import com.testinc.centralizedpaymentsystem.entity.Payments;
import com.testinc.centralizedpaymentsystem.repository.AccountsRepository;
import com.testinc.centralizedpaymentsystem.repository.PaymentsRepository;
import com.testinc.centralizedpaymentsystem.service.PaymentService;
import com.testinc.centralizedpaymentsystem.utils.AppUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    PaymentsRepository paymentsRepository;
    AccountsRepository accountsRepository;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public PaymentServiceImpl(PaymentsRepository paymentsRepository,
                              AccountsRepository accountsRepository) {
        this.paymentsRepository = paymentsRepository;
        this.accountsRepository = accountsRepository;

    }

    @Override
    public void saveUnProcessedOnlinePayments(ConsumerRecords<String, String> consumerRecords) {
        consumerRecords.forEach(record -> {
            try {
                PaymentDTO paymentDTO = mapper.readValue(record.value(), PaymentDTO.class);
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccountId());

                if (!accountById.isPresent()) {
                    System.out.println("account is not present");
                } else {
                    Payments payments = AppUtils.onlinePaymentDTOToEntity(paymentDTO);
                    payments.setAccounts(accountById.get());
                    paymentsRepository.save(payments);
                }


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void saveOfflinePayments(ConsumerRecords<String, String> consumerRecords) {
        consumerRecords.forEach(record -> {
            try {
                PaymentDTO paymentDTO = mapper.readValue(record.value(), PaymentDTO.class);
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccountId());

                if (!accountById.isPresent()) {
                    System.out.println("account is not present");
                } else {
                    Payments payments = AppUtils.offlinePaymentDTOToEntity(paymentDTO);
                    payments.setAccounts(accountById.get());
                    paymentsRepository.save(payments);
                }


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
