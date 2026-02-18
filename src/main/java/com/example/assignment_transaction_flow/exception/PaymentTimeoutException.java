package com.example.assignment_transaction_flow.exception;

public class PaymentTimeoutException extends RuntimeException {
    public PaymentTimeoutException(String message) {
        super(message);
    }
}
