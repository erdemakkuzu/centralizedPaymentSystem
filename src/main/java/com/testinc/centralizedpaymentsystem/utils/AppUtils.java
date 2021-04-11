package com.testinc.centralizedpaymentsystem.utils;

import com.testinc.centralizedpaymentsystem.dto.ErrorLog;
import com.testinc.centralizedpaymentsystem.dto.PaymentDTO;
import com.testinc.centralizedpaymentsystem.entity.LogHistory;
import com.testinc.centralizedpaymentsystem.entity.Payments;

import java.sql.Timestamp;

public class AppUtils {

    public static Payments onlinePaymentDTOToEntity(PaymentDTO paymentDTO){
        Payments payments = new Payments();
        payments.setPaymentId(paymentDTO.getPayment_id());
        payments.setPaymentType(paymentDTO.getPayment_type());
        payments.setProcessed(false);
        payments.setValid(false);
        payments.setAmount(paymentDTO.getAmount());
        payments.setCreditCard(paymentDTO.getCredit_card());
        payments.setCreatedOn(new Timestamp(System.currentTimeMillis()));


        return payments;
    }

    public static PaymentDTO paymentEntityToDTO(Payments payments){

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPayment_id(payments.getPaymentId());
        paymentDTO.setPayment_type(payments.getPaymentType());
        paymentDTO.setAccount_id(payments.getAccounts().getAccountId());
        paymentDTO.setAmount(payments.getAmount());
        paymentDTO.setCredit_card(payments.getCreditCard());
        paymentDTO.setCreatedOn(payments.getCreatedOn());

        return paymentDTO;

    }


    public static Payments offlinePaymentDTOToEntity(PaymentDTO paymentDTO) {
        Payments payments = new Payments();
        payments.setPaymentId(paymentDTO.getPayment_id());
        payments.setPaymentType(paymentDTO.getPayment_type());
        payments.setProcessed(true);
        payments.setValid(true);
        payments.setAmount(paymentDTO.getAmount());
        payments.setCreditCard(paymentDTO.getCredit_card());
        payments.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return payments;


    }

    public static ErrorLog logHistoryEntityToDTO(LogHistory logHistory) {
        ErrorLog errorLog = new ErrorLog(logHistory.getPaymentId(),
                logHistory.getErrorType(),
                logHistory.getErrorDescription());

        return errorLog;
    }
}
