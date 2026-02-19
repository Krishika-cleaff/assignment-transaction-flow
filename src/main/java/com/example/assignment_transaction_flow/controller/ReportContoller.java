package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.dto.DailyReportResponse;
import com.example.assignment_transaction_flow.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportContoller {
    private final ReportService reportService;

    public ReportContoller(ReportService reportService){
        this.reportService = reportService;
    }

    @GetMapping("/{date}")
    public ResponseEntity<DailyReportResponse> getReportByDate(
            @PathVariable String date
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        DailyReportResponse report = reportService.generateReportByDate(parsedDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/today")
    public ResponseEntity<DailyReportResponse> getTodayReport() {
        DailyReportResponse report =
                reportService.generateReportByDate(LocalDate.now());
        return ResponseEntity.ok(report);
    }

}
