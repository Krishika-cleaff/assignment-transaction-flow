package com.example.assignment_transaction_flow.dto;

import com.example.assignment_transaction_flow.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportResponse {
    private LocalDate date;
    private long totalSuccess;
    private Map<String, Long> failedByReason;
    private Map<PaymentMethod, BigDecimal> revenueByMethod;

}
