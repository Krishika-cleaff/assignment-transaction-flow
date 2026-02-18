package com.example.assignment_transaction_flow.dto;

import com.example.assignment_transaction_flow.model.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private PaymentMethod paymentMethod;

    // Card
    private String cardNumber;
    private String cvv;

    // UPI
    private String upiId;
    private String upiPin;
}
