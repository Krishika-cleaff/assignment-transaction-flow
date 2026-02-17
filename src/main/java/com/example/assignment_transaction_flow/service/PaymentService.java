package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.model.Payment;
import com.example.assignment_transaction_flow.model.PaymentMethod;
import com.example.assignment_transaction_flow.repository.PaymentRepo;

public class PaymentService {
    private final PaymentRepo paymentRepo;

    public PaymentService(PaymentRepo paymentRepo){
        this.paymentRepo = paymentRepo;
    }

    public void initiatePayment(String orderId, PaymentMethod paymentMethod){}

    public void processPayment(Payment payment){}

    public void retryPayment(String orderId, PaymentMethod paymentMethod){}
}
