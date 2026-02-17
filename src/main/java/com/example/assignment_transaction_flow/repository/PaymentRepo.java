package com.example.assignment_transaction_flow.repository;

import com.example.assignment_transaction_flow.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends MongoRepository<Payment,String > {
}
