# Zorvyn Backend

Zorvyn is a Spring Boot backend for a finance management system with JWT authentication, role-based access control, financial record management, dashboard analytics, validation, and global exception handling.

## Overview

The project is built as an interview-ready backend that focuses on:

- clean layered architecture
- secure stateless authentication with JWT
- RBAC using Spring Security method-level authorization
- financial record CRUD with filtering, search, pagination, and soft delete
- dashboard APIs for totals, balances, category summaries, and recent transactions
- production-style error handling with a consistent response shape

## Architecture

The application follows a layered structure:

```text
Controller -> Service -> Repository -> Database
```

### Key design decisions

- Controllers handle HTTP concerns and return `ResponseEntity<?>`.
- Services contain validation and business logic.
- Repositories use Spring Data JPA for persistence and aggregation queries.
- DTOs are used for requests and responses to not expose entities directly.
- Soft delete is used for financial records through the `deleted` flag.
- Audit timestamps are inherited from `BaseEntity`.
- Role checks are enforced with `@PreAuthorize(...)`.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- MySQL
- H2 Database for tests
- Jakarta Validation
- Springdoc OpenAPI / Swagger UI
- Lombok
- Maven

## Core Features

### Authentication and RBAC

- `POST /api/auth/register` registers a user
- `POST /api/auth/login` returns a JWT access token
- Stateless JWT authentication via a custom filter
- Passwords are stored with BCrypt
- Method-level authorization with predefined roles:
  - `ADMIN`: full access
  - `ANALYST`: records, users listing, dashboard access
  - `VIEWER`: read-only record access plus recent transactions

Note: self-registration currently creates users with the `VIEWER` role by default.

### User Management

- create user
- list users with pagination
- update user profile, role, and status
- toggle active/inactive status

### Financial Records

- create, list, update, and soft delete records
- pagination and sorting
- keyword search
- filtering by date, category, and type
- each record stores its creator

### Dashboard APIs

- total income
- total expense
- balance
- total active record count
- category-wise summary
- recent transactions

### Validation and Error Handling

- request DTO validation with Jakarta Validation
- global exception handling through `GlobalExceptionHandler`
- consistent error payload using `ErrorResponseDto`

Example error response:

```json
{
  "timestamp": "2026-04-06T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "POST /api/records"
}
```

## Domain Model

### User

- `id`
- `name`
- `email`
- `password`
- `role`
- `status`
- `createdAt`
- `updatedAt`

### FinancialRecord

- `id`
- `amount`
- `type`
- `category`
- `date`
- `description`
- `createdBy`
- `deleted`
- `createdAt`
- `updatedAt`

Relationship:

- one user can create many financial records

## API Summary

### Auth

```text
POST /api/auth/register
POST /api/auth/login
```

### Users

```text
POST /api/users
GET  /api/users?page=0&size=10&sort=asc
PUT  /api/users/{userId}
PUT  /api/users/{userId}/toggle-status
```

### Records

```text
POST   /api/records
GET    /api/records?page=0&size=10&sortBy=date&sortDirection=desc
GET    /api/records/{recordId}
GET    /api/records/search?keyword=rent&page=0&size=10
GET    /api/records/filter?date=2026-03-01&category=Salary&type=INCOME&page=0&size=10
PUT    /api/records/{recordId}
DELETE /api/records/{recordId}
```

### Dashboard

```text
GET /api/dashboard/summary
GET /api/dashboard/categories?type=EXPENSE
GET /api/dashboard/recent-transactions?limit=5
```

## Security Model

- All endpoints are secured by default.
- Public routes:
  - `/api/auth/**`
  - `/v3/api-docs/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
- Authorization is enforced with `@PreAuthorize(...)`.
- Inactive users cannot successfully log in.

## Profiles and Configuration

The app uses `SPRING_PROFILES_ACTIVE` and defaults to `dev`.

### Common environment variables

- `SPRING_PROFILES_ACTIVE` default: `dev`
- `JWT_SECRET` default: `u76XpY7N2mR+5jB/9lK0PzX4W6q8V9Y2S1H3D5F7G9J=`
- `JWT_EXPIRATION_MS` default: `86400000`

### `dev`

- port `8080`
- requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `SWAGGER_SERVER_URL`
- Swagger enabled
- log file: `logs/zorvyn-dev.log`

### `test`

- port `8081`
- in-memory H2 database
- Swagger disabled
- log file: `logs/zorvyn-test.log`

### `prod`

- port from `SERVER_PORT` defaulting to `8080`
- requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- Hibernate runs with `validate` (SQL schema should be readily available. Sample `data.sql` available.)
- Swagger disabled
- log file: `/var/log/zorvyn/zorvyn.log`

## Local Setup

### 1. Clone and enter the project

```bash
git clone https://github.com/avinashee0012/zorvyn.git
cd zorvyn
```

### 2. Configure environment

For the default `dev` profile, set:

```bash
DB_URL=your_mysql_url (e.g., mysql://localhost:3306/zorvyn)
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION_MS=86400000
SPRING_PROFILES_ACTIVE=dev
SWAGGER_SERVER_URL=your_backend_url (e.g., http://localhost:8080)
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Or package and run the jar:

```bash
mvn clean package
java -jar target/zorvyn-1.0.0.jar
```

### 4. Open Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## Build and Test

```bash
mvn clean verify
mvn clean package
```

Unit tests are available for:

- `UserServiceImpl`
- `RecordServiceImpl`
- `DashboardServiceImpl`
- `RecordController`
- `FinancialRecordRepository`

## Seed Data

The repository includes `data.sql` with sample users and records for local/demo usage.

Sample seeded accounts:

- `admin@zorvyn.dev`
- `analyst@zorvyn.dev`
- `viewer@zorvyn.dev`

The current project notes indicate the shared seeded password is:

```text
Password@123
```

## Assumptions and Trade-offs

- Roles are predefined and not dynamically managed.
- JWT authentication is intentionally simple and does not include refresh tokens.
- Financial records use soft delete instead of permanent deletion.
- Dashboard logic is implemented through repository/service aggregation rather than advanced reporting infrastructure.
- Registration is open and defaults new users to `VIEWER`.

## Future Improvements

- refresh tokens
- Docker support
- broader integration test coverage
- better filtering
