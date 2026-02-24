package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InvalidCredentialsException;
import com.example.assignment_transaction_flow.exception.PaymentTimeoutException;
import com.example.assignment_transaction_flow.model.Payment;
import com.example.assignment_transaction_flow.model.PaymentMethod;
import com.example.assignment_transaction_flow.model.PaymentStatus;
import com.example.assignment_transaction_flow.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void initiatePayment_success() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId("order123");
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setCardNumber("1234567812345678");
        request.setCvv("123");

        Payment payment = Payment.builder()
                .id("pay123")
                .orderId("order123")
                .paymentMethod(PaymentMethod.CARD)
                .amount(new BigDecimal("500"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(paymentService.initiatePayment(Mockito.any(PaymentRequest.class)))
                .thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pay123"))
                .andExpect(jsonPath("$.orderId").value("order123"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void initiatePayment_invalidCredentials_shouldReturn400() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId("order123");

        Mockito.when(paymentService.initiatePayment(Mockito.any(PaymentRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void initiatePayment_timeout_shouldReturn408() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId("order123");

        Mockito.when(paymentService.initiatePayment(Mockito.any(PaymentRequest.class)))
                .thenThrow(new PaymentTimeoutException("Payment timeout"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isRequestTimeout())
                .andExpect(content().string("Payment timeout"));
    }

    @Test
    void retryPayment_success() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId("order123");
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setUpiId("test@upi");
        request.setUpiPin("1234");

        Payment payment = Payment.builder()
                .id("retry123")
                .orderId("order123")
                .paymentMethod(PaymentMethod.UPI)
                .amount(new BigDecimal("300"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(paymentService.retryPayment(Mockito.any(PaymentRequest.class)))
                .thenReturn(payment);

        mockMvc.perform(post("/api/payments/retry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("retry123"));
    }
}