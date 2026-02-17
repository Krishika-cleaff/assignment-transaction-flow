package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.exception.InvalidOrderException;
import com.example.assignment_transaction_flow.model.Order;
import com.example.assignment_transaction_flow.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order){
        try {
            Order generatedOrder = orderService.createOrder(order);
            return ResponseEntity
                    .status(201)
                    .body(generatedOrder);
        }catch(InvalidOrderException e){
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id){
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        }catch (RuntimeException e){
            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());
        }
    }
}
