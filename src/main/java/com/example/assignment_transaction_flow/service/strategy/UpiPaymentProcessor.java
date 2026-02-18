package com.example.assignment_transaction_flow.service.strategy;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InsufficientBalanceException;
import com.example.assignment_transaction_flow.exception.InvalidCredentialsException;
import com.example.assignment_transaction_flow.exception.PaymentTimeoutException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {

    @Override
    public void process(PaymentRequest request,
                        BigDecimal balance,
                        BigDecimal amount) {

        if (request.getUpiId() == null || request.getUpiPin() == null) {
            throw new InvalidCredentialsException("UPI ID or PIN missing.");
        }

        if (Math.random() < 0.2) {
            throw new InvalidCredentialsException("Invalid UPI credentials.");
        }

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        if (Math.random() < 0.1) {
            throw new PaymentTimeoutException("Payment gateway timeout.");
        }
    }
}
