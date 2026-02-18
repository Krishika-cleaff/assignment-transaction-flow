package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InsufficientBalanceException;
import com.example.assignment_transaction_flow.exception.InvalidCredentialsException;
import com.example.assignment_transaction_flow.exception.InvalidPaymentAttemptException;
import com.example.assignment_transaction_flow.exception.PaymentTimeoutException;
import com.example.assignment_transaction_flow.model.*;
import com.example.assignment_transaction_flow.repository.PaymentRepo;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessor;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessorFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final OrderService orderService;
    private final PaymentProcessorFactory processorFactory;

    public PaymentService(PaymentRepo paymentRepo,OrderService orderService,PaymentProcessorFactory processorFactory){
        this.paymentRepo = paymentRepo;
        this.orderService = orderService;
        this.processorFactory = processorFactory;
    }

    public Payment initiatePayment(PaymentRequest paymentRequest){
        Order order = orderService.getOrderById(paymentRequest.getOrderId());

        if(order.getStatus()== OrderStatus.SUCCESS) throw new InvalidPaymentAttemptException("Order already placed.");
        if(order.getStatus()==OrderStatus.FAILED) throw new InvalidPaymentAttemptException("Order failed.");

        Payment payment = new Payment();
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getAmount());
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setCreatedAt(LocalDateTime.now());

        payment = paymentRepo.save(payment);

        try{
            PaymentProcessor processor = processorFactory.getProcessor(paymentRequest.getPaymentMethod());

            processor.process(paymentRequest, order.getCustomer().getBalance(),order.getAmount());

            orderService.updateCustomerBalance(paymentRequest.getOrderId(),order.getCustomer().getBalance().subtract(order.getAmount()));
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            orderService.updateOrderStatus(paymentRequest.getOrderId(),OrderStatus.SUCCESS);
            paymentRepo.save(payment);
        }
        catch (InvalidCredentialsException |
               InsufficientBalanceException |
               PaymentTimeoutException e) {

            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());

            throw e;
        }

        return paymentRepo.save(payment);
    }

    public Payment retryPayment(PaymentRequest request) {

        Order order = orderService.getOrderById(request.getOrderId());

        if (order.getStatus() == OrderStatus.SUCCESS) {
            throw new InvalidPaymentAttemptException("Order already successful.");
        }

        return initiatePayment(request);
    }

}
