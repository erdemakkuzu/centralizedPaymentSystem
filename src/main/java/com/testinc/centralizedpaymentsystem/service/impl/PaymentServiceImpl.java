package com.testinc.centralizedpaymentsystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testinc.centralizedpaymentsystem.constans.PaymentError;
import com.testinc.centralizedpaymentsystem.dto.ErrorLogDTO;
import com.testinc.centralizedpaymentsystem.dto.PaymentDTO;
import com.testinc.centralizedpaymentsystem.entity.Accounts;
import com.testinc.centralizedpaymentsystem.entity.LogHistory;
import com.testinc.centralizedpaymentsystem.entity.Payments;
import com.testinc.centralizedpaymentsystem.repository.AccountsRepository;
import com.testinc.centralizedpaymentsystem.repository.LogHistoryRepository;
import com.testinc.centralizedpaymentsystem.repository.PaymentsRepository;
import com.testinc.centralizedpaymentsystem.service.PaymentService;
import com.testinc.centralizedpaymentsystem.utils.AppUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${log.posting.size}")
    private Integer logPostingSize;

    @Value("${payment.api.gateway.url}")
    private String paymentApiGateWayURL;

    @Value("${payment.error.log.url}")
    private String paymentErrorLogUrl;

    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);


    PaymentsRepository paymentsRepository;
    AccountsRepository accountsRepository;
    LogHistoryRepository logHistoryRepository;
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public PaymentServiceImpl(PaymentsRepository paymentsRepository,
                              AccountsRepository accountsRepository,
                              LogHistoryRepository logHistoryRepository,
                              RestTemplate restTemplate) {
        this.paymentsRepository = paymentsRepository;
        this.accountsRepository = accountsRepository;
        this.restTemplate = restTemplate;
        this.logHistoryRepository = logHistoryRepository;

    }

    @Override
    public void saveUnProcessedOnlinePayments(ConsumerRecords<String, String> consumerRecords) {

        for (ConsumerRecord<String, String> record : consumerRecords) {
            try {
                PaymentDTO paymentDTO = mapper.readValue(record.value(), PaymentDTO.class);
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccount_id());
                Optional<Payments> paymentsForUniqueCheck = paymentsRepository.findById(paymentDTO.getPayment_id());

                if (!accountById.isPresent()) {
                    logErrorToDatabase(paymentDTO.getPayment_id(),
                            PaymentError.ACCOUNT_NOT_FOUND.getError(),
                            PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription());
                } else if (paymentsForUniqueCheck.isPresent()) {
                    logErrorToDatabase(paymentDTO.getPayment_id(),
                            PaymentError.PAYMENT_ID_IS_NOT_UNIQUE.getError(),
                            PaymentError.PAYMENT_ID_IS_NOT_UNIQUE.getErrorDescription());

                } else {
                    Payments payments = AppUtils.onlinePaymentDTOToEntity(paymentDTO);
                    payments.setAccounts(accountById.get());
                    paymentsRepository.save(payments);
                }

            } catch (JsonProcessingException e) {
                logger.error(PaymentError.KAFKA_JSON_PARSING_ERROR.getErrorDescription(), e);
            }
        }
    }

    private void logErrorToDatabase(String payment_id, String error, String errorDescription) {
        LogHistory logHistory = new LogHistory();
        logHistory.setPaymentId(payment_id);
        logHistory.setErrorType(error);
        logHistory.setErrorDescription(errorDescription);
        logHistory.setPosted(false);

        logHistoryRepository.save(logHistory);
    }

    @Override
    public void saveOfflinePayments(ConsumerRecords<String, String> consumerRecords) {
        consumerRecords.forEach(record -> {
            try {
                PaymentDTO paymentDTO = mapper.readValue(record.value(), PaymentDTO.class);
                Optional<Accounts> accountById = accountsRepository.findById(paymentDTO.getAccount_id());

                if (!accountById.isPresent()) {
                    logErrorToDatabase(paymentDTO.getPayment_id(),
                            PaymentError.ACCOUNT_NOT_FOUND.getError(),
                            PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription());
                } else {
                    Payments payments = AppUtils.offlinePaymentDTOToEntity(paymentDTO);
                    payments.setAccounts(accountById.get());
                    paymentsRepository.save(payments);
                    updateAccountsLastPaymentDate(paymentDTO);
                }

            } catch (JsonProcessingException e) {
                logger.error(PaymentError.KAFKA_JSON_PARSING_ERROR.getErrorDescription(), e);

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

    @Override
    public void postLogs() {
        Pageable firstPageWithGivenElements = PageRequest.of(0, logPostingSize);
        List<LogHistory> logsToPost = logHistoryRepository.findByPosted(false, firstPageWithGivenElements);

        logsToPost.forEach(logToPost -> {
            ErrorLogDTO errorLogDTO = AppUtils.logHistoryEntityToDTO(logToPost);
            postErrorLogToExternalAPI(errorLogDTO, logToPost);
        });

    }

    private void postErrorLogToExternalAPI(ErrorLogDTO errorLogDTO, LogHistory logHistory) {

        try {
            ResponseEntity<ErrorLogDTO> response = restTemplate.
                    postForEntity(paymentErrorLogUrl, errorLogDTO, ErrorLogDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logHistory.setPosted(true);
                logHistoryRepository.save(logHistory);
            } else {
                logger.error(PaymentError.EXTERNAL_LOG_API_DID_NOT_ACCEPT_REQUEST.getErrorDescription());
            }
        } catch (Exception e) {
            logger.error(PaymentError.EXTERNAL_LOG_API_DID_NOT_ACCEPT_REQUEST.getErrorDescription(), e);
        }

    }

    public void checkPaymentValidation(PaymentDTO paymentDTO) {

        try {
            ResponseEntity<String> response = restTemplate.
                    postForEntity(paymentApiGateWayURL, paymentDTO, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                updateValidatedPayment(paymentDTO);
                updateAccountsLastPaymentDate(paymentDTO);
            } else {
                updateInValidatedPayment(paymentDTO);
                logErrorToDatabase(paymentDTO.getPayment_id(),
                        PaymentError.UNSUCCESSFUL_HTTP_RESPONSE_FROM_API_GATEWAY.getError(),
                        PaymentError.UNSUCCESSFUL_HTTP_RESPONSE_FROM_API_GATEWAY.getErrorDescription());
            }

        } catch (HttpServerErrorException e) {
            updateInValidatedPayment(paymentDTO);
            logErrorToDatabase(paymentDTO.getPayment_id(),
                    PaymentError.TIMEOUT_API_GATEWAY.getError(),
                    PaymentError.TIMEOUT_API_GATEWAY.getErrorDescription());
        }

    }

    private void updateInValidatedPayment(PaymentDTO paymentDTO) {
        Optional<Payments> invalidPayment = paymentsRepository.findById(paymentDTO.getPayment_id());
        if (!invalidPayment.isPresent()) {
            logger.error(PaymentError.PAYMENT_NOT_FOUND.getErrorDescription());
        } else {
            invalidPayment.get().setValid(false);
            invalidPayment.get().setProcessed(true);
            paymentsRepository.save(invalidPayment.get());
        }
    }

    private void updateAccountsLastPaymentDate(PaymentDTO paymentDTO) {
        Optional<Accounts> accountToUpdate = accountsRepository.findById(paymentDTO.getAccount_id());
        if (!accountToUpdate.isPresent()) {
            logger.error(PaymentError.ACCOUNT_NOT_FOUND.getErrorDescription());
        } else {
            accountToUpdate.get().setLastPaymentDate(new Date());
            accountsRepository.save(accountToUpdate.get());
        }
    }

    private void updateValidatedPayment(PaymentDTO paymentDTO) {
        Optional<Payments> validPayment = paymentsRepository.findById(paymentDTO.getPayment_id());
        if (!validPayment.isPresent()) {
            logger.error(PaymentError.PAYMENT_NOT_FOUND.getErrorDescription());
        } else {
            validPayment.get().setValid(true);
            validPayment.get().setProcessed(true);
            paymentsRepository.save(validPayment.get());
        }
    }
}

