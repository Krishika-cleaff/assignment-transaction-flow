package com.example.assignment_transaction_flow.exception;

public class InvalidPaymentAttemptException extends RuntimeException {
    public InvalidPaymentAttemptException(String message) {
        super(message);
    }
}
