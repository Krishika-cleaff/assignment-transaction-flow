package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InsufficientBalanceException;
import com.example.assignment_transaction_flow.exception.InvalidPaymentAttemptException;
import com.example.assignment_transaction_flow.model.*;
import com.example.assignment_transaction_flow.repository.PaymentRepo;
import com.example.assignment_transaction_flow.repository.TransactionRepo;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessor;
import com.example.assignment_transaction_flow.service.strategy.PaymentProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentProcessorFactory processorFactory;

    @Mock
    private PaymentProcessor processor;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request;
    private Order order;

    @BeforeEach
    void setUp() {
        request = new PaymentRequest();
        request.setOrderId("order123");
        request.setPaymentMethod(PaymentMethod.UPI);

        Customer customer = new Customer();
        customer.setBalance(new BigDecimal("1000"));

        order = new Order();
        order.setId("order123");
        order.setAmount(new BigDecimal("500"));
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(customer);
    }


    @Test
    void initiatePaymentShouldReturnSuccess() {

        when(orderService.getOrderById("order123")).thenReturn(order);
        when(processorFactory.getProcessor(PaymentMethod.UPI)).thenReturn(processor);

        when(paymentRepo.save(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(transactionRepo.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.initiatePayment(request);

        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
    }


    @Test
    void initiatePaymentShouldReturnInsufficientBalance() {

        when(orderService.getOrderById("order123")).thenReturn(order);
        when(processorFactory.getProcessor(PaymentMethod.UPI)).thenReturn(processor);

        when(paymentRepo.save(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(transactionRepo.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(processor)
                .process(any(), any(), any());

        assertThrows(InsufficientBalanceException.class, () ->
                paymentService.initiatePayment(request)
        );
    }


    @Test
    void initiatePaymentShouldReturnOrderAlreadySuccess() {

        order.setStatus(OrderStatus.SUCCESS);
        when(orderService.getOrderById("order123")).thenReturn(order);

        assertThrows(InvalidPaymentAttemptException.class, () ->
                paymentService.initiatePayment(request)
        );

        verifyNoInteractions(paymentRepo);
        verifyNoInteractions(transactionRepo);
    }

    @Test
    void retryPayment_success() {

        when(orderService.getOrderById("order123")).thenReturn(order);
        when(processorFactory.getProcessor(PaymentMethod.UPI)).thenReturn(processor);

        when(paymentRepo.save(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(transactionRepo.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.retryPayment(request);

        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
    }

}
