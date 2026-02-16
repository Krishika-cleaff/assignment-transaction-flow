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
@Document(collection = "payment")
public class Payment {
    @Id
    private String id;
    private String orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String failureReason;
    private LocalDateTime createdAt;

}
