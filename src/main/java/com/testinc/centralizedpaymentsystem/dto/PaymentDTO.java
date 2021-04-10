package com.testinc.centralizedpaymentsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentDTO {

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("account_id")
    private Integer accountId;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("credit_card")
    private String creditCard;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("delay")
    private Integer delay;

}
