package com.example.assignment_transaction_flow.repository;

import com.example.assignment_transaction_flow.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends MongoRepository<Order,String> {
}
