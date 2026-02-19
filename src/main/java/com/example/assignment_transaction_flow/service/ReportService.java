package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.dto.DailyReportResponse;
import com.example.assignment_transaction_flow.model.PaymentMethod;
import com.example.assignment_transaction_flow.model.Transaction;
import com.example.assignment_transaction_flow.model.TransactionStatus;
import com.example.assignment_transaction_flow.repository.TransactionRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {
    private final TransactionRepo transactionRepo;

    public ReportService(TransactionRepo transactionRepo){
        this.transactionRepo = transactionRepo;
    }

    public long getTotalSuccessfulTransaction(List<Transaction> transactionList){
        return transactionList.stream()
                .filter(t -> t.getTransactionStatus()== TransactionStatus.SUCCESS)
                .count();
    }

    public Map<String, Long> getFailedTransaction(List<Transaction> transactionList){
        return transactionList.stream()
                .filter(t -> t.getTransactionStatus()== TransactionStatus.FAILED)
                .collect(Collectors.groupingBy(
                        Transaction::getMessage,
                        Collectors.counting()
                ));
    }

    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMtd(List<Transaction> transactionList){

         return transactionList.stream()
                .filter(t -> t.getTransactionStatus()== TransactionStatus.SUCCESS)
                .collect(Collectors.groupingBy(
                        Transaction::getPaymentMethod,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    public DailyReportResponse generateReportByDate(LocalDate date){
        List<Transaction> transactionList = transactionRepo.findAll();
        List<Transaction> transactionsByDate = transactionList.stream()
                .filter(t-> t.getTimestamp().toLocalDate().equals(date))
                .toList();
        return new DailyReportResponse(
                date,
                getTotalSuccessfulTransaction(transactionsByDate),
                getFailedTransaction(transactionsByDate),
                getRevenueByPaymentMtd(transactionsByDate)
        );

    }

}
