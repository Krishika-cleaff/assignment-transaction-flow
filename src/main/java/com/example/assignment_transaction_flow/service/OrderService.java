package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.exception.InvalidOrderException;
import com.example.assignment_transaction_flow.model.Order;
import com.example.assignment_transaction_flow.repository.OrderRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.assignment_transaction_flow.model.OrderStatus.PENDING;

@Service
public class OrderService {
    private final OrderRepo orderRepo;

    public OrderService(OrderRepo orderRepo){
        this.orderRepo = orderRepo;
    }

    public Order createOrder(Order order){
        if(order.getAmount()==null || order.getAmount().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidOrderException("Amount cannot be null or zero.");
        if(order.getCustomer() == null){
            throw new InvalidOrderException("Valid customer should place the order.");
        }
        if(order.getStatus()!=null && order.getStatus() != PENDING){
            throw new InvalidOrderException("Order status should be PENDING.");
        }

        order.setStatus(PENDING);
        return orderRepo.save(order);
    }

    public Order getOrderById(String id){
        return orderRepo.findById(id).orElseThrow(()-> new RuntimeException("Order not found."));
    }

    public void updateOrderStatus(String id){}
}
