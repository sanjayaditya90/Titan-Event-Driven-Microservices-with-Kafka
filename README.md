# Titan-Event-Driven-Microservices-with-Kafka
A Spring Boot event-driven microservices application demonstrating asynchronous communication using Apache Kafka, payment processing with strategy pattern, notification and delivery workflows, consumer groups, idempotency, retry handling, and Dead Letter Topics (DLT).

# Titan Event-Driven Microservices

This project is an event-driven microservices application built around Apache Kafka.

## 1. Services

The project contains the following services:

- `titan-order-service`
- `titan-payment-service`
- `titan-notification-service`
- `titan-delivery-service`

The services communicate asynchronously through Kafka topics.

---

## 2. Architecture Overview

```text
                         +----------------------+
                         |   Order Service      |
                         |   localhost:8080     |
                         +----------+-----------+
                                    |
                                    | OrderCreatedEvent
                                    v
                           +----------------+
                           |     Kafka      |
                           +-------+--------+
                                   |
                    +--------------+--------------+
                    |                             |
                    v                             v
             order-created                 order-created
                    |                             |
                    v                             v
             Payment Service              Notification Service
                    |
                    | PaymentCreatedEvent
                    v
              payment-success
                    |
                    v
             Delivery Service
                    |
          +---------+----------+
          |                    |
          v                    v
   delivery-created      out-for-delivery
          |
          |                    |
          +---------+----------+
                    |
                    v
             order-delivered
```

The exact consumer-group assignment is controlled by the Kafka listener configuration in each service.

---

# 3. Prerequisites

Before starting the application, make sure the following are available:

- Java 17
- Maven
- Apache Kafka
- Kafka broker running on:

```text
localhost:9092
```

- A database configured for each service according to its `application.properties` / `application.yml`
- Postman for testing the REST API

---

# 4. Kafka Topics

The application uses the following Kafka topics:

| Topic | Purpose |
|---|---|
| `order-created` | Published when a new order is created |
| `payment-success` | Published when payment processing succeeds |
| `payment-failed` | Used for payment failure events |
| `delivery-created` | Used for delivery creation events |
| `out-for-delivery` | Used when an order goes out for delivery |
| `order-delivered` | Used when an order is delivered |

## Kafka topic flow

```text
order-created
      |
      +--------------------> Payment Service
      |
      +--------------------> Notification Service
      |
      v
Payment processing
      |
      +----> payment-success
      |
      +----> payment-failed
                    |
                    v
             Delivery Service
                    |
                    +----> delivery-created
                    |
                    +----> out-for-delivery
                    |
                    +----> order-delivered
```

---

# 5. Kafka Consumer Groups

The configured consumer groups are:

| Consumer Group | Description |
|---|---|
| `OS1` | Order-service consumer group |
| `OS2` | Order-service consumer group |
| `PS1` | Payment-service consumer group |
| `PS2` | Delivery-service consumer group |

> The exact topic-to-group mapping is determined by the `@KafkaListener` configuration in each service.

### Important Kafka concept

If multiple instances of the same service use the same consumer group, Kafka distributes partitions among those instances.

For example:

```text
                    order-created
                   /      |      \
             Partition 0  1       2
                  |       |       |
                  v       v       v
               PS1-1   PS1-2   PS1-3
```

All three Payment Service instances can use:

```text
groupId = PS1
```

This allows them to work as one consumer group.

---

# 6. Starting the Application

Start Kafka first.

Then start the microservices.

A typical startup order is:

```text
1. Kafka
2. Order Service
3. Payment Service
4. Notification Service
5. Delivery Service
```

The services can be started individually from their respective project directories.

Example:

```bash
mvn spring-boot:run
```

Run the command inside the required service directory.

Alternatively, run each `Spring Boot` application directly from the IDE.

---

# 7. Verify Kafka

Make sure the Kafka broker is available at:

```text
localhost:9092
```

The services should be able to connect to the broker before testing the order API.

---

# 8. Create an Order Using Postman

## API

```text
POST http://localhost:8080/titan/order/create
```

## Headers

```text
Content-Type: application/json
```

## Request Body

```json
{
  "customerId": "CUST1002",
  "customerName": "Rahul Sharma",
  "deliveryAddress": "123 MG Road, Bengaluru, Karnataka",
  "paymentMethod": "UPI",
  "email": "rahul.sharma@example.com",
  "mobileNo": "9876543210",
  "orderItemRequests": [
    {
      "productId": 101,
      "productName": "Ray-Ban Sunglasses",
      "quantity": 2,
      "amount": 2500.00
    },
    {
      "productId": 102,
      "productName": "Titan Watch",
      "quantity": 1,
      "amount": 4500.00
    }
  ]
}
```

---

# 9. Expected Event Flow

After sending the request, the expected flow is:

```text
Postman
   |
   | POST /titan/order/create
   v
Order Service
   |
   | Create Order
   | Save Order
   |
   | Publish OrderCreatedEvent
   v
Kafka: order-created
   |
   +----------------------------+
   |                            |
   v                            v
Payment Service          Notification Service
   |
   | Process payment
   |
   +----------------------+
   |                      |
   v                      v
payment-success      payment-failed
   |
   v
Delivery Service
   |
   | Process delivery
   |
   +------------------+
   |                  |
   v                  v
delivery-created   out-for-delivery
                         |
                         v
                  order-delivered
```

---

# 10. Payment Service

The Payment Service consumes events from:

```text
order-created
```

using its configured consumer group:

```text
PS1
```

Payment processing supports the payment processor implementations configured in the service.

The project contains:

```text
PaymentProcessor
    |
    +-- UpiPaymentProcessor
    |
    +-- CardPaymentProcessor
```

The processor is selected based on the payment method in the order event.

After successful payment processing, the Payment Service publishes a payment event to:

```text
payment-success
```

Payment processing uses an event ID to support duplicate-event detection.

---

# 11. Notification Service

The Notification Service consumes order events and processes notification-related actions.

The project contains notification processors such as:

```text
NotificationProcessor
    |
    +-- EmailNotificationProcessor
    |
    +-- SMSNotificationProcessor
```

The service also maintains processed-event information for duplicate-event handling.

---

# 12. Delivery Service

The Delivery Service consumes the payment-success event using:

```text
PS2
```

It processes the delivery status and publishes delivery-related events.

Possible delivery topics are:

```text
delivery-created
out-for-delivery
order-delivered
```

The delivery service also maintains processed-event information for duplicate-event handling.

---

# 13. Idempotency

The services use an event ID to identify an event.

For example:

```text
eventId = 5904b158-e71d-4937-814e-c74cbbe01544
```

Before processing an event, the service can check whether the event has already been processed.

Conceptually:

```text
Receive event
     |
     v
Check eventId
     |
     +---- Already processed ---> Skip
     |
     +---- New event -----------> Process
                                  |
                                  v
                           Save eventId
```

This prevents the same Kafka event from being processed multiple times when it is redelivered.

---

# 14. Multiple Service Instances

The Payment Service can be scaled horizontally.

For example:

```text
Payment Service Instance 1
        |
        +-- Consumer group PS1

Payment Service Instance 2
        |
        +-- Consumer group PS1

Payment Service Instance 3
        |
        +-- Consumer group PS1
```

All instances use the same consumer group.

Kafka distributes topic partitions among the instances.

Each application instance may also have its own Kafka producer. This is normal because each Spring Boot instance runs in a separate JVM.

The important point is to use a Spring-managed `KafkaTemplate` rather than creating a new Kafka producer for every message.

---

# 15. Error Handling and Retry

Kafka consumers can be configured with Spring Kafka error handling.

The general flow is:

```text
Kafka Message
      |
      v
Consumer
      |
      v
Processing
      |
   Exception
      |
      v
DefaultErrorHandler
      |
      +---- Retry
      |
      +---- Retry
      |
      v
     DLT
```

The retry count depends on the `DefaultErrorHandler` / `BackOff` configuration in the respective service.

A failed message can therefore be retried before being published to its configured Dead Letter Topic (DLT).

---

# 16. DLT Topics

If Dead Letter Topic handling is configured, a topic can be created using the original topic name with a `.DLT` suffix.

Examples:

```text
order-created.DLT
payment-success.DLT
delivery-created.DLT
out-for-delivery.DLT
order-delivered.DLT
```

Only include/create the DLTs that are actually configured by the corresponding service.

---

# 17. Testing the Complete Flow

### Step 1 — Start Kafka

Verify that Kafka is available:

```text
localhost:9092
```

### Step 2 — Start all services

Start:

```text
titan-order-service
titan-payment-service
titan-notification-service
titan-delivery-service
```

### Step 3 — Send the Postman request

```text
POST http://localhost:8080/titan/order/create
```

with the JSON body from Section 8.

### Step 4 — Check Order Service logs

Verify that the order is created and the `order-created` event is published.

### Step 5 — Check Payment Service logs

Verify that the event is consumed and payment processing occurs.

For UPI:

```text
paymentMethod = UPI
```

### Step 6 — Check Notification Service logs

Verify that the notification processors receive/process the order event.

### Step 7 — Check Delivery Service logs

Verify that the `payment-success` event is consumed and delivery processing occurs.

### Step 8 — Verify Kafka topics

Check the relevant topics:

```text
order-created
payment-success
payment-failed
delivery-created
out-for-delivery
order-delivered
```

---

# 18. Project Structure

## Order Service

```text
titan-order-service
└── src/main/java/com/titan
    ├── kafka/producer/service
    │   └── OrderProducer.java
    ├── order/controller
    │   └── OrderController.java
    ├── order/entity
    │   ├── OrderEntity.java
    │   └── OrderItemEntity.java
    ├── order/event
    │   └── OrderCreatedEvent.java
    ├── order/repository
    │   └── OrderRepository.java
    ├── order/request
    │   ├── OrderRequest.java
    │   └── OrderItemRequest.java
    ├── order/response
    │   ├── OrderResponse.java
    │   └── OrderItemResponse.java
    └── order/service
        └── OrderService.java
```

## Payment Service

```text
titan-payment-service
└── src/main/java
    ├── kafka/payment/config
    │   └── KafkaConsumerConfig.java
    ├── kafka/payment/service
    │   ├── PaymentConsumer.java
    │   └── PaymentProducer.java
    ├── payment/entity
    │   └── PaymentEntity.java
    ├── payment/event
    │   ├── OrderCreatedEvent.java
    │   ├── PaymentCreatedEvent.java
    │   └── PaymentProcessEvent.java
    ├── payment/processor
    │   ├── PaymentProcessor.java
    │   ├── UpiPaymentProcessor.java
    │   └── CardPaymentProcessor.java
    ├── payment/repository
    │   └── PaymentProcessedEventRepository.java
    ├── payment/response
    │   └── PaymentResponse.java
    └── payment/service
        └── PaymentService.java
```

## Notification Service

```text
titan-notification-service
└── src/main/java
    ├── kafka/notification/config
    │   └── KafkaConsumerConfig.java
    ├── kafka/notification/service
    │   └── NotificationConsumer.java
    ├── notification/event
    │   ├── NotificationProcessEvent.java
    │   └── OrderCreatedEvent.java
    ├── notification/processor
    │   ├── NotificationProcessor.java
    │   ├── EmailNotificationProcessor.java
    │   └── SMSNotificationProcessor.java
    ├── notification/repository
    │   └── NotificationProcessedEventRepository.java
    └── notification/service
        └── NotificationService.java
```

## Delivery Service

```text
titan-delivery-service
└── src/main/java
    ├── delivery/event
    │   ├── DeliveryProcessEvent.java
    │   └── PaymentCreatedEvent.java
    ├── delivery/repository
    │   └── DeliveryProcessedEventRepository.java
    ├── delivery/service
    │   └── DeliveryService.java
    └── kafka/delivery
        ├── config
        │   └── KafkaConsumerConfig.java
        └── service
            ├── DeliveryConsumer.java
            └── DeliveryProducer.java
```

---

# 19. Quick Reference

| Component | Value |
|---|---|
| Order API | `POST http://localhost:8080/titan/order/create` |
| Kafka Broker | `localhost:9092` |
| Order Topic | `order-created` |
| Payment Success Topic | `payment-success` |
| Payment Failed Topic | `payment-failed` |
| Delivery Created Topic | `delivery-created` |
| Out for Delivery Topic | `out-for-delivery` |
| Order Delivered Topic | `order-delivered` |
| Order Consumer Groups | `OS1`, `OS2` |
| Payment Consumer Group | `PS1` |
| Delivery Consumer Group | `PS2` |
| Java Version | 17 |

---

## 20. End-to-End Summary

```text
                    POST /titan/order/create
                              |
                              v
                       ORDER SERVICE
                              |
                              | OrderCreatedEvent
                              v
                       order-created
                         /        \
                        /          \
                       v            v
                PAYMENT SERVICE   NOTIFICATION
                       |
                       | PaymentCreatedEvent
                       v
                  payment-success
                       |
                       v
                 DELIVERY SERVICE
                       |
          +------------+------------+
          |            |            |
          v            v            v
   delivery-created  out-for-    order-delivered
                     delivery
```

This is the primary event-driven flow of the Titan microservices application.

