package com.example.assignment_transaction_flow.service;

import com.example.assignment_transaction_flow.exception.InvalidOrderException;
import com.example.assignment_transaction_flow.model.*;
import com.example.assignment_transaction_flow.repository.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setBalance(new BigDecimal("1000"));

        order = new Order();
        order.setId("order123");
        order.setAmount(new BigDecimal("500"));
        order.setCustomer(customer);
        order.setItems(List.of("Item1"));
        order.setStatus(null);
    }

    @Test
    void createOrderShouldReturnSuccess() {

        when(orderRepo.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order saved = orderService.createOrder(order);

        assertEquals(OrderStatus.PENDING, saved.getStatus());
        verify(orderRepo).save(order);
    }

    @Test
    void createOrderShouldReturnInvalidAmount() {
        order.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(order));

        verifyNoInteractions(orderRepo);
    }

    @Test
    void createOrderShouldReturnNullCustomer() {
        order.setCustomer(null);

        assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(order));

        verifyNoInteractions(orderRepo);
    }

    @Test
    void createOrderShouldReturnWrongStatus() {
        order.setStatus(OrderStatus.SUCCESS);

        assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(order));

        verifyNoInteractions(orderRepo);
    }

    @Test
    void createOrderShouldReturnNullItems() {
        order.setItems(null);

        assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(order));

        verifyNoInteractions(orderRepo);
    }



    @Test
    void getOrderByIdShouldReturnSuccess() {
        when(orderRepo.findById("order123"))
                .thenReturn(Optional.of(order));

        Order found = orderService.getOrderById("order123");

        assertEquals("order123", found.getId());
    }

    @Test
    void getOrderByIdShouldReturnNotFound() {
        when(orderRepo.findById("order123"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.getOrderById("order123"));
    }

    @Test
    void updateOrderStatusShouldReturnSuccess() {
        when(orderRepo.findById("order123"))
                .thenReturn(Optional.of(order));

        orderService.updateOrderStatus("order123", OrderStatus.SUCCESS);

        assertEquals(OrderStatus.SUCCESS, order.getStatus());
        verify(orderRepo).save(order);
    }

    @Test
    void updateCustomerBalanceShouldReturnSuccess() {
        when(orderRepo.findById("order123"))
                .thenReturn(Optional.of(order));

        BigDecimal newBalance = new BigDecimal("500");

        orderService.updateCustomerBalance("order123", newBalance);

        assertEquals(newBalance, order.getCustomer().getBalance());
        verify(orderRepo).save(order);
    }
}
