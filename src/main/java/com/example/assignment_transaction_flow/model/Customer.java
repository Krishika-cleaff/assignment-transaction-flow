package com.example.assignment_transaction_flow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    private String id;
    private String customerName;
    private String email;
    private BigDecimal balance;
}
