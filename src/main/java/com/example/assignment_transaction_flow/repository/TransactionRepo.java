package com.example.assignment_transaction_flow.repository;

import com.example.assignment_transaction_flow.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction,String > {
}
