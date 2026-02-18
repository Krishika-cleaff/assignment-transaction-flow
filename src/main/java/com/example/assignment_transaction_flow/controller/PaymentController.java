package com.example.assignment_transaction_flow.controller;

import com.example.assignment_transaction_flow.dto.PaymentRequest;
import com.example.assignment_transaction_flow.exception.InsufficientBalanceException;
import com.example.assignment_transaction_flow.exception.InvalidCredentialsException;
import com.example.assignment_transaction_flow.exception.InvalidPaymentAttemptException;
import com.example.assignment_transaction_flow.exception.PaymentTimeoutException;
import com.example.assignment_transaction_flow.model.Payment;
import com.example.assignment_transaction_flow.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @PostMapping
    public ResponseEntity<?> initatePayment(@RequestBody PaymentRequest paymentRequest){
        try{
            Payment payment = paymentService.initiatePayment(paymentRequest);
            return ResponseEntity
                    .ok(payment);
        }catch(InvalidCredentialsException |
               InsufficientBalanceException |
               InvalidPaymentAttemptException e){
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        }catch (PaymentTimeoutException e){
            return ResponseEntity
                    .status(408)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/retry")
    public ResponseEntity<?> retryPayment(
            @RequestBody PaymentRequest retryRequest) {

        try {
            Payment payment =
                    paymentService.retryPayment(retryRequest);

            return ResponseEntity.ok(payment);

        } catch (InvalidCredentialsException |
                 InsufficientBalanceException |
                 InvalidPaymentAttemptException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (PaymentTimeoutException e) {

            return ResponseEntity
                    .status(408)
                    .body(e.getMessage());
        }
    }
}
