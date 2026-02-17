package com.example.assignment_transaction_flow.model;


import lombok.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private Customer customer;
    private List<String> items;
    private BigDecimal amount;
    private OrderStatus status;

}
