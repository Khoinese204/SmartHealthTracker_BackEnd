# Smart Health Tracker – Backend

Backend REST API cho ứng dụng **Smart Health Tracker** – một mobile app theo dõi sức khỏe sử dụng cảm biến trên điện thoại (nhịp tim, bước chân, giấc ngủ, GPS,…).  
Hệ thống được xây dựng bằng **Spring Boot**, cung cấp các API để lưu trữ, xử lý và đồng bộ dữ liệu sức khỏe từ mobile.

## 1. Công nghệ sử dụng

- Spring Boot 3.x
- Java 21
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- Spring Security (JWT custom)
- Lombok
- Validation
- Springdoc OpenAPI (Swagger)

## 2. Cấu trúc hệ thống

```
com.example.smarthealth.backend
 ├── config
 ├── auth
 ├── health
 └── common
```

## 3. Cài đặt & chạy dự án

### Yêu cầu

- JDK 21
- PostgreSQL
- Maven
- VSCode

### Bước 1 – Clone

```
git clone https://github.com/Khoinese204/SmartHealthTracker_BackEnd.git

```

### Bước 2 – Cấu hình database (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smarthealth
    username: postgres
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080
```

### Bước 3 – Chạy dự án

```
mvn spring-boot:run
```

Mặc định chạy tại `http://localhost:8080`.
