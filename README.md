# Spring Boot JWT + Rate Limiting POC

A Java 21 / Spring Boot sample that:

- authenticates users with username/password;
- generates and validates JWT bearer tokens;
- protects `/api/alerts`;
- limits each authenticated user (or fallback IP) to 10 requests per 60 seconds;
- returns HTTP `429` with `Retry-After` when the limit is exceeded.

## Requirements

- JDK 21
- Maven 3.9+
- Postman or curl

## Run

```bash
mvn spring-boot:run
```

The POC has two in-memory users:

- `admin` / `Admin@123`
- `operator` / `Operator@123`

## 1. Generate JWT

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

Copy `accessToken` from the response.

## 2. Call protected API

```bash
curl http://localhost:8080/api/alerts \
  -H "Authorization: Bearer YOUR_TOKEN"
```

The response includes `X-Rate-Limit-Remaining`. The 11th request within 60 seconds returns `429 Too Many Requests`.

## Configuration

See `src/main/resources/application.properties`.

For a real environment, set a private Base64-encoded key:

```powershell
$env:JWT_SECRET="YOUR_BASE64_SECRET"
mvn spring-boot:run
```

## Important

This rate limiter is intentionally in-memory for a single-instance POC. For multiple Kubernetes replicas, use a shared store such as Redis so all pods enforce one common limit.
