# Lending Platform Backend

## Overview

This project is a simplified, production-inspired backend for a digital lending platform built using Spring Boot. It demonstrates how to design a scalable, maintainable system that can evolve over time.

The system supports:

* Loan product configuration
* Customer management
* Loan lifecycle management
* Event-driven communication

---

## 🧠 Architectural Approach

The system is designed with **clean architecture principles** and **separation of concerns**:

Controller → Service → Repository → Database
Service → Outbox → Event Publisher → Kafka (optional)

Key focus areas:

* Maintainability
* Extensibility
* Reliability
* Developer experience

---

## ⚙️ Tech Stack

* Java 21
* Spring Boot
* Spring Data JPA
* H2 (default, for easy local setup)
* Kafka (optional)
* Springdoc OpenAPI (Swagger)
* JUnit + Mockito

---

## 🚀 Running the Application

### Prerequisites

* Java 21
* Gradle (or use the included Gradle Wrapper)

### Steps

Using Gradle Wrapper (recommended):

```bash
./gradlew clean build
./gradlew bootRun
```

Or if Gradle is installed globally:

```bash
gradle clean build
gradle bootRun
```

### Access Points

* API Base URL: [http://localhost:5000](http://localhost:5000)
* Swagger UI: [http://localhost:5000/swagger-ui/index.html](http://localhost:5000/swagger-ui/index.html)
* H2 Console: [http://localhost:5000/h2-console](http://localhost:5000/h2-console)

---

## 📦 Core Modules

### 1. Customer Management

* Stores customer profile
* Maintains credit limits
* Enables future extensibility (risk scoring, KYC)

### 2. Loan Product Configuration

* Defines loan rules (interest rate, tenure, limits)
* Designed to support multiple product types
* can be extended using groovy for different rules - down side is this can be easily manipulated by engineers

### 3. Loan Lifecycle Management

Loan states:

* CREATED
* APPROVED
* DISBURSED
* CLOSED
* OVERDUE
* REJECTED

Handles:

* Loan creation
* Repayments
* Balance tracking

---

## 📡 Event-Driven Architecture

The system emits domain events for key actions:

* LOAN_CREATED
* REPAYMENT_MADE

### Design

Instead of tightly coupling services:

Core Service → Event → Consumer

This enables:

* Notifications
* Analytics
* Fraud detection

without modifying core logic.
Logging is enabled though not system wide - areas for improvement.

---

## 🔁 Outbox Pattern (Reliability)

To ensure consistency between database writes and event publishing:

1. Business data and event are stored in the same transaction
2. Events are saved in an `outbox_events` table
3. A scheduled job publishes events asynchronously

### Benefits

* Prevents data loss
* Ensures eventual consistency
* Safe retries on failure

---

## 🔄 Event Publishing Strategy

The system supports two modes:

### Kafka Enabled

* Events are published to Kafka topics

### Kafka Disabled (Default)

* Events are logged locally

This allows the system to:

* Run without external dependencies
* Scale when Kafka is introduced

---

## 📥 DTO + Validation Layer

All incoming requests use DTOs with validation:

* Prevents invalid data entering the system
* Keeps domain models clean

Example validations:

* Required fields
* Minimum loan amounts

---

## ⚠️ Exception Handling

Centralized exception handling using `@RestControllerAdvice`

Features:

* Consistent API error responses
* Separation of business vs system errors
* Validation error mapping

---

## 📘 API Documentation (Swagger)

Swagger is integrated using Springdoc OpenAPI.

Features:

* Interactive API testing
* Request/response documentation
* Validation visibility

---

## 🧪 Testing Strategy

The system includes:

### Unit Tests

* Service layer business logic

### Controller Tests

* Validation and request handling

### Integration-style Tests

* Outbox event publishing

Goal:

* Focus on critical flows rather than exhaustive coverage

---

## 🧱 Design Patterns Used

* **Outbox Pattern** → Reliable event publishing
* **Strategy Pattern** → Kafka vs Local event publisher
* **DTO Pattern** → API-layer abstraction
* **Global Exception Handling** → Consistent error responses

---

## 🔮 Future Improvements

* Kafka consumer for notifications (SMS/Email)
* Scheduled jobs for overdue loans
* Exhaustive Audit logging
* Fraud detection -> use of AI can assist
* Repayment schedules
* Multi-currency support
* Authentication & authorization
* Dockerized deployment

---

## ⚡ Key Design Decisions

* Kept loan lifecycle simple but extensible
* Avoided over-engineering (no complex state machine yet)
* Decoupled event handling from core logic
* Designed system to degrade gracefully without Kafka

---

## 💬 Final Note

This system is intentionally designed as a **foundation** that can evolve into a full-scale lending platform. Emphasis was placed on clarity, extensibility, and production readiness over feature completeness.

------------------------------------------------------
------------------------------------------------------

## 📂 Final Project Structure

```
lending-platform/
├── src/
│   ├── main/
│   │   ├── java/lending/platform/lendingplatform/
│   │   └── resources/
│   └── test/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── gradle/wrapper/
```

## 💡 Recommendation

Always use: Considering we have gradle wrapper

```bash
./gradlew bootRun
```


