

# Payment Processing & Order Management System

## Project Overview

This project is a backend Java application that simulates a real-world **payment processing and order management system** similar to those used in e-commerce and fintech platforms.

The system handles:

* Order creation and retrieval
* Payment processing with multiple payment methods
* Payment failure handling with custom exceptions
* Daily transaction reporting and analytics
* Transaction logging
* Clean layered architecture with unit testing


---

## Architecture Overview

The application follows a clean layered architecture:

```
controller  →  service  →  domain/model
                  ↓
               factory
                  ↓
              strategy
```

### Layers

* **Controller Layer**

  * Handles REST API requests
  * Delegates business logic to services
  * Returns appropriate HTTP responses

* **Service Layer**

  * Contains core business logic
  * Processes orders and payments
  * Generates reports
  * Handles validation and exceptions

* **Model Layer**

  * Core entities like Order, Transaction, Customer
  * Enums such as PaymentMethod, OrderStatus

* **Factory Layer**

  * Creates payment processor instances based on payment method

* **Strategy Layer**

  * Implements payment processing logic for different methods

---

## Project Structure

```
com.example.assignment_transaction_flow
│
├── controller
│   ├── OrderController
│   ├── PaymentController
│   └── ReportController
│
├── service
│   ├── OrderService
│   ├── PaymentService
│   ├── ReportService
    └── strategy
│       ├── PaymentProcessor
│       ├── PaymentProcessorFactory
│       ├── CardPaymentProcessor
│       ├── UpiPaymentProcessor
│       └── WalletPaymentProcessor
│
├── model
│   ├── Order
│   ├── Transaction
│   ├── Customer
│   ├── PaymentMethod
│   ├── Payment
│   ├── PaymentStatus
│   ├── TransactionStatus
│   └── OrderStatus
│
├── dto
│   └── DailyReportResponse
│   └── PaymentRequest
│
├── exception
│   ├── InvalidOrderException
│   ├── PaymentTimeoutException
│   ├── InsufficientBalanceException
│   ├── InsufficientCredentialsException
│   ├── OrderNotFoundException
│   └── InvalidPaymentAttemptException
│
└── repository
    ├── OrderRepo
    ├── PaymentRepo
    └── TransactionRepo
```

---

## Features Implemented

### Order Management

* Create new orders
* Validate order data
* Retrieve order by ID
* Update order status based on payment result

---

### Payment Processing

* Multiple payment methods supported:

  * CARD
  * UPI
  * WALLET
* Strategy pattern used for payment logic
* Factory pattern used to create payment processors
* Custom exception hierarchy for failure cases
* Meaningful error messages

---

### Payment Failure & Retry Handling

Handles failures such as:

* Insufficient balance
* Invalid payment method
* Generic payment failure

Failures are:

* Logged
* Captured in transaction records
* Reflected in reporting

---

### Reporting & Analytics

Daily reports include:

* Total successful payments
* Failed transactions grouped by reason
* Revenue grouped by payment method

Implemented using:

* Java Collections
* Stream API (`groupingBy`, `counting`, `summing`)
* DTO response model

---

### Transaction Logging

Each payment attempt is:

* Recorded as a transaction
* Stored for reporting and audit purposes

---

## Design Patterns Used

### Strategy Pattern

Used to encapsulate payment processing logic per payment method.

```
PaymentProcessor (interface)
    ├── CardPaymentProcessor
    ├── UpiPaymentProcessor
    └── WalletPaymentProcessor
```

### Factory Pattern

Creates appropriate PaymentProcessor based on PaymentMethod.

```
PaymentProcessorFactory.getProcessor(PaymentMethod)
```

### Exception Hierarchy

Custom business exceptions for clean failure handling.

---


## Technologies Used

* Java 17
* Spring Boot 3.5.10
* Spring Boot Starter Web
* Spring Boot Starter Data MongoDB
* Spring Boot DevTools
* Lombok
* Spring Boot Starter Test (JUnit 5, Mockito, MockMvc)
* Maven

---


## Testing

The project includes:

* Controller layer tests using `@WebMvcTest`
* Service layer unit tests
* Mocking with Mockito
* MockMvc for REST endpoint testing

All tests run successfully and validate:

* Successful order creation
* Error scenarios
* Reporting logic correctness

---

## How to Build & Run

### 1. Clone Repository

```bash
git clone <your-repository-link>
cd assignment-transaction-flow
```

### 2. Build Project

```bash
mvn clean install
```

### 3. Run Application

```bash
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## Sample API Endpoints

```
==============================
ORDER ENDPOINTS
==============================

POST   /api/orders
        → Create a new order
        → Returns: 201 Created
        → 400 Bad Request if order is invalid

GET    /api/orders/{id}
        → Get order by ID
        → Returns: 200 OK
        → 404 Not Found if order does not exist


==============================
PAYMENT ENDPOINTS
==============================

POST   /api/payments
        → Initiate a payment
        → Returns: 200 OK
        → 400 Bad Request for:
              - InvalidCredentialsException
              - InsufficientBalanceException
              - InvalidPaymentAttemptException
        → 408 Request Timeout for:
              - PaymentTimeoutException

POST   /api/payments/retry
        → Retry a failed payment
        → Returns: 200 OK
        → 400 Bad Request for business validation errors
        → 408 Request Timeout for payment timeout


==============================
REPORT ENDPOINTS
==============================

GET    /api/reports/{date}
        → Generate report for specific date
        → Date format: YYYY-MM-DD
        → Example:
              GET /api/reports/2026-02-24
        → Returns: DailyReportResponse

GET    /api/reports/today
        → Generate report for current date
        → Returns: DailyReportResponse
```


---

## Conclusion

This project simulates a real-world backend payment and order workflow system with clean architecture, proper design patterns, exception handling, and reporting capabilities.


