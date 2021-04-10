package com.testinc.centralizedpaymentsystem.utils;

import com.testinc.centralizedpaymentsystem.dto.PaymentDTO;
import com.testinc.centralizedpaymentsystem.entity.Payments;

import java.sql.Timestamp;

public class AppUtils {

    public static Payments onlinePaymentDTOToEntity(PaymentDTO paymentDTO){
        Payments payments = new Payments();
        payments.setPaymentId(paymentDTO.getPaymentId());
        payments.setPaymentType(paymentDTO.getPaymentType());
        payments.setProcessed(false);
        payments.setValid(false);
        payments.setAmount(paymentDTO.getAmount());
        payments.setCreditCard(paymentDTO.getCreditCard());
        payments.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return payments;
    }

    public static Payments offlinePaymentDTOToEntity(PaymentDTO paymentDTO) {
        Payments payments = new Payments();
        payments.setPaymentId(paymentDTO.getPaymentId());
        payments.setPaymentType(paymentDTO.getPaymentType());
        payments.setProcessed(true);
        payments.setValid(true);
        payments.setAmount(paymentDTO.getAmount());
        payments.setCreditCard(paymentDTO.getCreditCard());
        payments.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return payments;


    }
}
