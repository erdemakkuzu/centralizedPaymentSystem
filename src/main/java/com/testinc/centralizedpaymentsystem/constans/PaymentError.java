package com.testinc.centralizedpaymentsystem.constans;

public enum PaymentError {

    ACCOUNT_NOT_FOUND("database", "Could not find an account with given id in the payment"),
    UNSUCCESSFUL_HTTP_RESPONSE_FROM_API_GATEWAY("network", "Response from API Gateway was unsuccessful"),
    TIMEOUT_API_GATEWAY("network", "API Gateway did not respond in defined time interval"),
    KAFKA_JSON_PARSING_ERROR("kafka", "Could not parse json from producer"),
    PAYMENT_ID_IS_NOT_UNIQUE("database", "There is already a payment with same id. Payment id is not unique."),
    EXTERNAL_LOG_API_DID_NOT_ACCEPT_REQUEST("other", "External log API did not accept request. Post operation will be tried again.");

    private final String error;
    private final String errorDescription;

    PaymentError(String errorType, String errorDescription) {
        this.error = errorType;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
