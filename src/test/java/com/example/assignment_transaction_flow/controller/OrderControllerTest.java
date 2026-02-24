package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.exception.InvalidOrderException;
import com.example.assignment_transaction_flow.model.Customer;
import com.example.assignment_transaction_flow.model.Order;
import com.example.assignment_transaction_flow.model.OrderStatus;
import com.example.assignment_transaction_flow.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_success() throws Exception {

        Order order = new Order();
        order.setId("order123");
        order.setAmount(new BigDecimal("500"));
        order.setStatus(OrderStatus.PENDING);

        Customer customer = new Customer();
        customer.setCustomerName("Krishika");
        customer.setBalance(new BigDecimal("1000"));

        order.setCustomer(customer);
        order.setItems(List.of("Item1"));

        Mockito.when(orderService.createOrder(Mockito.any(Order.class)))
                .thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("order123"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void createOrder_invalidOrder_shouldReturn400() throws Exception {

        Order order = new Order();

        Mockito.when(orderService.createOrder(Mockito.any(Order.class)))
                .thenThrow(new InvalidOrderException("Invalid order data"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order data"));
    }

    @Test
    void getOrderById_success() throws Exception {

        Order order = new Order();
        order.setId("order123");
        order.setAmount(new BigDecimal("500"));
        order.setStatus(OrderStatus.PENDING);

        Mockito.when(orderService.getOrderById("order123"))
                .thenReturn(order);

        mockMvc.perform(get("/api/orders/order123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order123"));
    }

    @Test
    void getOrderById_notFound_shouldReturn404() throws Exception {

        Mockito.when(orderService.getOrderById("order123"))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/api/orders/order123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found"));
    }
}