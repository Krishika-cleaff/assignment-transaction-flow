package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.dto.DailyReportResponse;
import com.example.assignment_transaction_flow.model.PaymentMethod;
import com.example.assignment_transaction_flow.model.Transaction;
import com.example.assignment_transaction_flow.model.TransactionStatus;
import com.example.assignment_transaction_flow.repository.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private ReportService reportService;

    private Transaction successUPI;
    private Transaction successCard;
    private Transaction failedTxn;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        successUPI = new Transaction();
        successUPI.setTransactionStatus(TransactionStatus.SUCCESS);
        successUPI.setPaymentMethod(PaymentMethod.UPI);
        successUPI.setAmount(new BigDecimal("500"));
        successUPI.setTimestamp(LocalDateTime.now());

        successCard = new Transaction();
        successCard.setTransactionStatus(TransactionStatus.SUCCESS);
        successCard.setPaymentMethod(PaymentMethod.CARD);
        successCard.setAmount(new BigDecimal("300"));
        successCard.setTimestamp(LocalDateTime.now());

        failedTxn = new Transaction();
        failedTxn.setTransactionStatus(TransactionStatus.FAILED);
        failedTxn.setPaymentMethod(PaymentMethod.UPI);
        failedTxn.setAmount(new BigDecimal("200"));
        failedTxn.setMessage("Insufficient balance");
        failedTxn.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getTotalSuccessfulTransactionShouldReturnCorrectCount() {
        List<Transaction> list = List.of(successUPI, successCard, failedTxn);

        long count = reportService.getTotalSuccessfulTransaction(list);

        assertEquals(2, count);
    }

    @Test
    void getFailedTransactionShouldGroupByFailureReason() {
        List<Transaction> list = List.of(successUPI, failedTxn);

        Map<String, Long> result = reportService.getFailedTransaction(list);

        assertEquals(1, result.get("Insufficient balance"));
    }

    @Test
    void getRevenueByPaymentMtdShouldCalculateRevenueCorrectly() {
        List<Transaction> list = List.of(successUPI, successCard, failedTxn);

        Map<PaymentMethod, BigDecimal> revenue =
                reportService.getRevenueByPaymentMtd(list);

        assertEquals(new BigDecimal("500"), revenue.get(PaymentMethod.UPI));
        assertEquals(new BigDecimal("300"), revenue.get(PaymentMethod.CARD));
    }

    @Test
    void generateReportByDateShouldGenerateCorrectReport() {

        when(transactionRepo.findAll())
                .thenReturn(List.of(successUPI, successCard, failedTxn));

        DailyReportResponse report =
                reportService.generateReportByDate(today);

        // Total success
        assertEquals(2, report.getTotalSuccess());

        // Failed grouped
        assertEquals(1,
                report.getFailedByReason().get("Insufficient balance"));

        // Revenue
        assertEquals(new BigDecimal("500"),
                report.getRevenueByMethod().get(PaymentMethod.UPI));

        assertEquals(new BigDecimal("300"),
                report.getRevenueByMethod().get(PaymentMethod.CARD));
    }

    @Test
    void generateReportByDateShouldReturnEmptyReportWhenNoTransactions() {

        when(transactionRepo.findAll()).thenReturn(List.of());

        DailyReportResponse report =
                reportService.generateReportByDate(today);

        assertEquals(0, report.getTotalSuccess());
        assertTrue(report.getFailedByReason().isEmpty());
        assertTrue(report.getRevenueByMethod().isEmpty());
    }
}
