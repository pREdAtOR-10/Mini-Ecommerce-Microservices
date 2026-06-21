# Mini-Ecommerce Microservices Backend 🚀

A robust, cloud-native, and fault-tolerant e-commerce backend built with **Java 17**, **Spring Boot 4.x**, and **Spring Cloud 2025**. This project serves as a hands-on architectural blueprint demonstrating foundational microservices patterns, including centralized service discovery, modern load-balanced routing, centralized edge security, and resilient fault isolation.

---

## 🏗️ System Architecture & Flow

The system consists of 4 decoupled Spring Boot services orchestrating a synchronous, blocking communication lifecycle over an isolated Docker virtual network:

1. **`discovery-server` (Netflix Eureka Server)**: Acts as the central registry (phonebook) where all services register their dynamic network identities upon bootstrap.
2. **`api-gateway` (Spring Cloud Gateway - Servlet Stack)**: The single public-facing gatekeeper that dynamically routes external API consumer traffic based on paths via load-balanced lookups (`lb://`).
3. **`product-service`**: Manages inventory details utilizing an in-memory H2 database seeded automatically with runtime dummy data.
4. **`order-service`**: Orchestrates checkout operations. It initiates a synchronous inter-service request to `product-service` via a modernized, load-balanced `RestClient` to validate catalog details prior to handling state persistence.

### Request Journey Flowchart

[ Outside World ] ---> Hits Public Gateway (Port 8080)
│
▼
┌─────────────────┐
│   API Gateway   │ Checks Path & queries Eureka
└────────┬────────┘
│
(Internal Network)
▼
┌─────────────────┐
│  order-service  │ Intercepted by Resilience4j
└────────┬────────┘
│
[ Is Product Service Healthy? ]
/

YES  /                 \  NO (Circuit Opens)
▼                   ▼
Fetches Product Details    Executes Local Fallback Method

from product-service (8081)  (Graceful degraded message)

---

## 🛠️ Tech Stack & Architecture Patterns

* **Language**: Java 17 (Eclipse Temurin JDK)
* **Framework**: Spring Boot 4.0.6 & Spring Framework 7.x
* **Microservices Patterns**:
  * **Service Discovery**: Netflix Eureka Client/Server
  * **API Gateway Routing**: Spring Cloud Gateway MVC (Servlet Stack) with explicit programmatic predicates
  * **Dynamic Load Balancing**: Spring Cloud LoadBalancer integrated via manual functional interceptors (`LoadBalancerInterceptor`)
  * **Resilience & Fault Tolerance**: Resilience4j Circuit Breaker (Count-Based Sliding Window Engine)
* **Database Layer**: Spring Data JPA & In-Memory H2 Database Engine
* **Containerization**: Docker & Docker Compose (Bridge Network Isolation)

---

## 🚀 Getting Started

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* Maven 3.x or IntelliJ IDEA (for local development adjustments).

### Running the Ecosystem in 1-Click
Because the system is fully containerized, you do not need to install Java or H2 locally. The network topology maps only the required entry boundaries (`8080` and `8761`) to your host machine while insulating backend databases.

1. **Compile and Package executable JAR binaries**:
   Navigate to each microservice root directory containing a `pom.xml` file and compile the source:
   ```bash
   mvn clean package -DskipTests

   Spin up the multi-container cluster:
Navigate to your root directory containing the docker-compose.yml file and execute:

Bash
docker compose up --build
Verify Node Registrations:
Open your browser and navigate to the Eureka Dashboard at http://localhost:8761. Ensure that API-GATEWAY, PRODUCT-SERVICE, and ORDER-SERVICE all report an active status state of UP.
