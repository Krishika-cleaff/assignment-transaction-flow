package com.example.assignment_transaction_flow.service.strategy;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import java.math.BigDecimal;

public interface PaymentProcessor {
    void process(PaymentRequest request,
                 BigDecimal balance,
                 BigDecimal amount);
}
