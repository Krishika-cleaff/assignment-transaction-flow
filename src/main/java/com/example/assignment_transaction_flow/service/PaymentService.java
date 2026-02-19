package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InsufficientBalanceException;
import com.example.assignment_transaction_flow.exception.InvalidCredentialsException;
import com.example.assignment_transaction_flow.exception.InvalidPaymentAttemptException;
import com.example.assignment_transaction_flow.exception.PaymentTimeoutException;
import com.example.assignment_transaction_flow.model.*;
import com.example.assignment_transaction_flow.repository.PaymentRepo;
import com.example.assignment_transaction_flow.repository.TransactionRepo;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessor;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessorFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final TransactionRepo transactionRepo;
    private final OrderService orderService;
    private final PaymentProcessorFactory processorFactory;

    public PaymentService(PaymentRepo paymentRepo,OrderService orderService,PaymentProcessorFactory processorFactory, TransactionRepo transactionRepo){
        this.paymentRepo = paymentRepo;
        this.orderService = orderService;
        this.processorFactory = processorFactory;
        this.transactionRepo = transactionRepo;
    }

    public Payment initiatePayment(PaymentRequest paymentRequest){
        Order order = orderService.getOrderById(paymentRequest.getOrderId());

        if(order.getStatus()== OrderStatus.SUCCESS) throw new InvalidPaymentAttemptException("Order already placed.");
        if(order.getStatus()==OrderStatus.FAILED) throw new InvalidPaymentAttemptException("Order failed.");

        Payment payment = Payment.builder()
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.getAmount())
                .orderId(paymentRequest.getOrderId())
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepo.save(payment);

        Transaction transaction = Transaction.builder()
                .orderId(paymentRequest.getOrderId())
                .paymentId(payment.getId())
                .amount(order.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .build();


        try{
            PaymentProcessor processor = processorFactory.getProcessor(paymentRequest.getPaymentMethod());

            processor.process(paymentRequest, order.getCustomer().getBalance(),order.getAmount());

            orderService.updateCustomerBalance(paymentRequest.getOrderId(),order.getCustomer().getBalance().subtract(order.getAmount()));
            orderService.updateOrderStatus(paymentRequest.getOrderId(),OrderStatus.SUCCESS);

            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setMessage("Transaction completed successfully.");
        }
        catch (InvalidCredentialsException |
               InsufficientBalanceException |
               PaymentTimeoutException e) {

            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());

            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setMessage(e.getMessage());

            throw e;
        }
        finally {
            paymentRepo.save(payment);
            transactionRepo.save(transaction);
        }

        return payment;
    }

    public Payment retryPayment(PaymentRequest request) {

        Order order = orderService.getOrderById(request.getOrderId());

        if (order.getStatus() == OrderStatus.SUCCESS) {
            throw new InvalidPaymentAttemptException("Order already successful.");
        }

        return initiatePayment(request);
    }

}
