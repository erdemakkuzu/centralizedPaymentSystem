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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${online.payments.process-size}")
    private Integer onlinePaymentProcessSize;

    @Value("${payment.api.gateway.url}")
    private String paymentApiGateWayURL;

    PaymentsRepository paymentsRepository;
    AccountsRepository accountsRepository;
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public PaymentServiceImpl(PaymentsRepository paymentsRepository,
                              AccountsRepository accountsRepository,
                              RestTemplate restTemplate) {
        this.paymentsRepository = paymentsRepository;
        this.accountsRepository = accountsRepository;
        this.restTemplate = restTemplate;

    }

    @Override
    public void saveUnProcessedOnlinePayments(ConsumerRecords<String, String> consumerRecords) {
        consumerRecords.forEach(record -> {
            try {
                PaymentDTO paymentDTO = mapper.readValue(record.value(), PaymentDTO.class);
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccount_id());

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
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccount_id());

                if (!accountById.isPresent()) {
                    System.out.println("account is not present");
                } else {
                    Payments payments = AppUtils.offlinePaymentDTOToEntity(paymentDTO);
                    payments.setAccounts(accountById.get());
                    paymentsRepository.save(payments);
                    updateAccountsLastPaymentDate(paymentDTO);
                }


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void processOnlinePayments() {

        Pageable firstPageWithGivenElements = PageRequest.of(0, onlinePaymentProcessSize);

        List<Payments> paymentsByProcessed =
                paymentsRepository.findByProcessed(false, firstPageWithGivenElements);

        paymentsByProcessed.stream().
                map(AppUtils::paymentEntityToDTO).
                forEach(this::checkPaymentValidation);
    }

    public void checkPaymentValidation(PaymentDTO paymentDTO) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.
                    postForEntity(paymentApiGateWayURL, paymentDTO, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                updateValidatedPayment(paymentDTO);
                updateAccountsLastPaymentDate(paymentDTO);
            } else {
                updateInValidatedPayment(paymentDTO);
                System.out.println("error");
            }

        } catch (HttpServerErrorException e) {
            System.out.println("error log");
            updateInValidatedPayment(paymentDTO);
        }

    }

    private void updateInValidatedPayment(PaymentDTO paymentDTO) {
        Optional<Payments> invalidPayment = paymentsRepository.findById(paymentDTO.getPayment_id());
        invalidPayment.get().setValid(false);
        invalidPayment.get().setProcessed(true);
        paymentsRepository.save(invalidPayment.get());
    }

    private void updateAccountsLastPaymentDate(PaymentDTO paymentDTO) {
        Optional<Accounts> accountToUpdate = accountsRepository.findById(paymentDTO.getAccount_id());
        accountToUpdate.get().setLastPaymentDate(new Date());
        accountsRepository.save(accountToUpdate.get());
    }

    private void updateValidatedPayment(PaymentDTO paymentDTO) {
        Optional<Payments> validPayment = paymentsRepository.findById(paymentDTO.getPayment_id());
        validPayment.get().setValid(true);
        validPayment.get().setProcessed(true);
        paymentsRepository.save(validPayment.get());
    }

}

