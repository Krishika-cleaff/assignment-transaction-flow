package com.example.assignment_transaction_flow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private TransactionStatus transactionStatus;
    private LocalDateTime timestamp;
    private String message;
}
