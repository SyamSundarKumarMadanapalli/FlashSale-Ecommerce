# ⚡ Flash Sale E‑commerce Platform

---

## 1. Problem Statement

Design a highly scalable, low-latency, fault-tolerant backend system for flash-sale e-commerce that can handle extreme traffic spikes while guaranteeing string inventory consistency ans reliable order processing.

---

## 2. Requirements Gathering

### 2.1 Functional Requirements

#### User
- Browse products
- View product details
- Participate in flash sales
- Purchase limited-stock items
- View order status
- Receive notifications for order success/failure

#### Flash Sale
- Limited inventory per product
- Very high concurrent purchase attempts
- First-come-first-served allocation

#### Order
- Create order
- Process payment asynchronously
- Maintain order lifecycle states

#### Inventory
- Real-time stock deduction
- Prevent overselling under concurrency

---

### 2.2 Non-Functional Requirements

| Category     | Requirement |
|--------------| ------------|
| Latency      | < 100ms for purshase API |
| Availability | >= 99.9% |
| Scalability | 100K+ concurrent users |
| Consistency | Strong consistency for inventory |
| Reliability | No duplicate orders |
| Fault Tolerance | Graceful degradation |
| Idempotency | Exactly-once order intent |
| Observability | Logs, metrics, tracing |
| security | Auth, rate limiting |

---

### 2.3 Assumptions

- Flash sales last minutes to hours
- Inventory is small but highly contended
- Users are pre-authenticated
- Payment gateway is mocked initially
- Eventual consistency acceptable except inventory

## 3. Back-of-the-Envelope Estimation

### Traffic
- Concurrent users: ~100K
- Purchase attempts: ~20K/sec
- Order creation peak: ~5K/sec
- Products per sale: ~10

### Storage
- Orders/day: ~1M
- Inventory records: small but hot
- Kafka events: append-only, high volume

### Key Insight
- Read heavy -> aggressive caching
- Write critical -> correctness over latency

---

## 4. High-Level Design (HLD)

