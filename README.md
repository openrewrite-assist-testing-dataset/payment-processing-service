# Payment Processing Service

A payment processing service built with Dropwizard 2.0.x, designed for testing modernization tools.

## Features

- **Dropwizard 2.0.x** framework
- **Multi-module architecture** (common, service, client)
- **PostgreSQL** database with Flyway migrations
- **OAuth2 JWT authentication** for security
- **JDBI** for database access
- **OpenAPI 3.0** documentation (older version)
- **Java 11** runtime

## Prerequisites

- Java 11 or higher
- Gradle 6.9
- PostgreSQL 9.6+

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd payment-processing-service
   ```

2. **Setup PostgreSQL database**
   ```sql
   CREATE DATABASE payments;
   CREATE USER payments WITH PASSWORD 'payments123';
   GRANT ALL PRIVILEGES ON DATABASE payments TO payments;
   ```

3. **Run database migrations**
   ```bash
   ./gradlew flywayMigrate
   ```

4. **Build the application**
   ```bash
   ./gradlew build
   ```

5. **Run the application**
   ```bash
   java -jar build/libs/payment-processing-service-*.jar server src/main/resources/config.yml
   ```

6. **Access the application**
   - API: http://localhost:8080/api/v1/payments
   - Admin: http://localhost:8080/admin

## Authentication

The API uses OAuth2 JWT tokens. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### Payments API
- `POST /api/v1/payments` - Create new payment
- `GET /api/v1/payments/{id}` - Get specific payment
- `GET /api/v1/payments/merchant/{merchantId}` - Get payments by merchant
- `POST /api/v1/payments/{id}/process` - Process payment
- `POST /api/v1/payments/{id}/cancel` - Cancel payment
- `POST /api/v1/payments/{id}/refund` - Refund payment

### Example Request
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "merchant-123",
    "amount": 100.00,
    "currency": "USD",
    "description": "Test payment"
  }'
```

## Modules

### Common
Contains shared models and utilities:
- `Payment` - Payment entity
- `PaymentStatus` - Payment status enum

### Service
Contains business logic:
- `PaymentService` - Core payment operations
- `PaymentDAO` - Database access layer

### Client
Contains HTTP client for external integrations:
- `PaymentClient` - HTTP client for payment API

## Testing

Run the test suite:
```bash
./gradlew test
```

## Docker

Build and run with Docker:
```bash
docker build -t payment-processing-service .
docker run -p 8080:8080 payment-processing-service
```

## Configuration

Database and OAuth2 settings are in `src/main/resources/config.yml`.

## Development Notes

This application uses intentionally outdated patterns for testing purposes:
- Dropwizard 2.0.x (could be updated to newer version)
- Older Jackson versions (2.12.x)
- Gradle 6.9 (older version)
- Legacy publishing configurations
- Older OpenAPI version (2.1.x)

## Health Check

Check application health:
```bash
curl http://localhost:8080/admin/healthcheck
```