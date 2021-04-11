package com.testinc.centralizedpaymentsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentDTO {

    @JsonProperty("payment_id")
    private String payment_id;

    @JsonProperty("account_id")
    private Integer account_id;

    @JsonProperty("payment_type")
    private String payment_type;

    @JsonProperty("credit_card")
    private String credit_card;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("delay")
    private Integer delay;

    private Timestamp createdOn;

}
