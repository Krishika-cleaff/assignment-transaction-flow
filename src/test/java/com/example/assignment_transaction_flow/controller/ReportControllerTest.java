package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.dto.DailyReportResponse;
import com.example.assignment_transaction_flow.model.PaymentMethod;
import com.example.assignment_transaction_flow.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportContoller.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void getReportByDate_success() throws Exception {

        LocalDate date = LocalDate.of(2026, 2, 24);

        DailyReportResponse response = new DailyReportResponse(
                date,
                15L,
                Map.of(
                        "INSUFFICIENT_BALANCE", 3L,
                        "TIMEOUT", 2L
                ),
                Map.of(
                        PaymentMethod.CARD, new BigDecimal("5000"),
                        PaymentMethod.UPI, new BigDecimal("3000")
                )
        );

        Mockito.when(reportService.generateReportByDate(date))
                .thenReturn(response);

        mockMvc.perform(get("/api/reports/2026-02-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-02-24"))
                .andExpect(jsonPath("$.totalSuccess").value(15))
                .andExpect(jsonPath("$.failedByReason.INSUFFICIENT_BALANCE").value(3))
                .andExpect(jsonPath("$.failedByReason.TIMEOUT").value(2))
                .andExpect(jsonPath("$.revenueByMethod.CARD").value(5000))
                .andExpect(jsonPath("$.revenueByMethod.UPI").value(3000));
    }

    @Test
    void getTodayReport_success() throws Exception {

        LocalDate today = LocalDate.now();

        DailyReportResponse response = new DailyReportResponse(
                today,
                10L,
                Map.of("INVALID_CREDENTIALS", 1L),
                Map.of(PaymentMethod.CARD, new BigDecimal("2000"))
        );

        Mockito.when(reportService.generateReportByDate(today))
                .thenReturn(response);

        mockMvc.perform(get("/api/reports/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSuccess").value(10))
                .andExpect(jsonPath("$.failedByReason.INVALID_CREDENTIALS").value(1))
                .andExpect(jsonPath("$.revenueByMethod.CARD").value(2000));
    }
}