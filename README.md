# Smart Health Tracker – Backend

A RESTful backend API for **Smart Health Tracker**, a mobile application designed to
collect, store, and process health-related data from smartphones
(heart rate, step count, sleep tracking, GPS location, etc.).

The system is built with **Spring Boot** following clean architecture principles,
optimized for mobile clients (React Native / Expo),
with a strong focus on security, scalability, and cloud deployment readiness.

---

## 🚀 Key Features

- User authentication using **custom JWT-based security**
- Health data management and time-series storage
- RESTful API design optimized for mobile applications
- Clear separation of layers: Controller – Service – Repository
- Database schema versioning with **Flyway**
- Integration with **Firebase Admin SDK**
- Image upload and management via **Cloudinary**
- Interactive API documentation with **Swagger / OpenAPI**
- Docker-ready for cloud deployment

---

## 🛠 Technology Stack

- **Java 21**
- **Spring Boot 3**
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security (JWT – custom implementation)
- PostgreSQL
- Flyway Migration
- Firebase Admin SDK
- Cloudinary
- Lombok
- Bean Validation
- Springdoc OpenAPI (Swagger)
- Docker

---

## 📂 Project Structure

```
src/main/java/com/example/smarthealth
├── controller     # REST API controllers
├── dto            # Request / Response DTOs
├── enums          # Enum definitions
├── model          # JPA entities
├── repository     # Data access layer
├── service        # Business logic
├── util           # Utility / helper classes
└── SmarthealthApplication.java
```

```
src/main/resources
├── db/migration   # Flyway migration scripts
├── static
├── templates
└── application.yml
```

---

## ▶️ Run Locally

### Prerequisites

- JDK 21
- PostgreSQL
- Maven

### Clone the repository

```bash
git clone https://github.com/Khoinese204/SmartHealthTracker_BackEnd.git
cd SmartHealthTracker_BackEnd
```

## ⚙️ Configuration

The application is fully configured via **environment variables**,
making it suitable for both local development and production deployment.

### Start the application

```bash
mvn spring-boot:run
```

The backend will be available at:

```
http://localhost:8080
```

---

## 📘 API Documentation

Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## ☁️ Deployment

- Backend service deployed on **Render**
- Database: **PostgreSQL (Render managed)**
- Dockerized Spring Boot application
- Production-ready configuration using environment variables (12-factor app)

> This project is deployed in a production-like environment
> and is publicly accessible via REST APIs.

---
