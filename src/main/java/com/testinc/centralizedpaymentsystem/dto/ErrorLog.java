package com.testinc.centralizedpaymentsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorLog {
    @JsonProperty("payment_id")
    private String payment_id;

    @JsonProperty("error_type")
    private String error;

    @JsonProperty("error_description")
    private String error_description;

    public ErrorLog() {

    }



    public ErrorLog(String payment_id, String error, String error_description) {
        this.payment_id = payment_id;
        this.error = error;
        this.error_description = error_description;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }
}
