# WireMock - External Credit Bureau Mock Server

This directory contains the **WireMock** infrastructure, declarative stubs, and response payloads used to simulate the external **Credit Bureau** API consumed by the application via the Spring Cloud OpenFeign declarative client ([`CustomerBureauFeignClient`](../app/src/main/java/com/example/templatejava/customer/infrastructure/webclient/bureau/CustomerBureauFeignClient.java)).

---

## 1. Directory Structure

```text
wiremock/
├── Dockerfile.wiremock       # Dockerfile to build an immutable, standalone WireMock image
├── README.md                 # Documentation and usage guide
├── __files/                  # Static and Handlebars-templated JSON response payloads
│   ├── bureau-customer-approved.json
│   ├── bureau-customer-default.json
│   ├── bureau-customer-not-found.json
│   └── bureau-customer-rejected.json
├── extensions/               # Standalone WireMock Java extensions
│   └── wiremock-jwt-extension-standalone-0.3.0.jar
└── mappings/                 # Declarative stub definitions and routing rules
    ├── bureau-customer-approved.json
    ├── bureau-customer-default.json
    ├── bureau-customer-not-found.json
    └── bureau-customer-rejected.json
```

---

## 2. Simulated API Contract

The mock server simulates the credit bureau customer report lookup endpoint:

- **Method**: `GET`
- **Path**: `/api/v1/bureau/customers/{customerId}`
- **Default Base URL**: `http://localhost:8081` (mapped to port `8080` inside the container via `WIREMOCK_PORT`)

### Mapped Scenarios

| Scenario | Path / Parameter | HTTP Status | Score | Bureau Status | Payload / File |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **Dynamic Default** | `/api/v1/bureau/customers/{any-id}` | `200 OK` | `820` | `APPROVED` | `__files/bureau-customer-default.json` *(dynamically extracts the customerId from the URL path using Handlebars `{{request.pathSegments.[4]}}`)* |
| **Explicit Approved** | `/api/v1/bureau/customers/cust-approved` | `200 OK` | `950` | `APPROVED` | `__files/bureau-customer-approved.json` |
| **Explicit Rejected** | `/api/v1/bureau/customers/cust-rejected` | `200 OK` | `380` | `REJECTED` | `__files/bureau-customer-rejected.json` |
| **Not Found** | `/api/v1/bureau/customers/cust-not-found` | `404 Not Found` | - | - | `__files/bureau-customer-not-found.json` |

---

## 3. How to Run

### Option A: Local Development via Docker Compose

The `wiremock` service is pre-configured in [`docker-compose.yaml`](../docker-compose.yaml) with live volume mounts:

```bash
# Start all local infrastructure (MongoDB, WireMock, SonarQube, Tempo)
task infra:up

# Or start only the WireMock service
docker compose up -d wiremock
```

The service is exposed on host port `8081` (configured via `WIREMOCK_PORT` in `.env`).

### Option B: Build and Run Standalone Container (Immutable Image for CI/CD)

Use the [`Dockerfile.wiremock`](./Dockerfile.wiremock) to build a self-contained image bundling all mappings, files, and extensions:

```bash
# From the project root:
docker build -f wiremock/Dockerfile.wiremock -t ms-wiremock:latest wiremock

# Run the standalone container:
docker run --rm -p 8081:8080 ms-wiremock:latest
```

---

## 4. Verification with `curl`

Once the container is up and running on port `8081`:

```bash
# 1. Dynamic query (echoes the customerId from URL path)
curl -i http://localhost:8081/api/v1/bureau/customers/cust-sync-1
# HTTP/1.1 200 OK
# {"customerId": "cust-sync-1", "score": 820, "status": "APPROVED"}

# 2. Explicit approved customer
curl -i http://localhost:8081/api/v1/bureau/customers/cust-approved
# HTTP/1.1 200 OK
# {"customerId": "cust-approved", "score": 950, "status": "APPROVED"}

# 3. Explicit rejected customer
curl -i http://localhost:8081/api/v1/bureau/customers/cust-rejected
# HTTP/1.1 200 OK
# {"customerId": "cust-rejected", "score": 380, "status": "REJECTED"}

# 4. Inexistent customer (404)
curl -i http://localhost:8081/api/v1/bureau/customers/cust-not-found
# HTTP/1.1 404 Not Found
# {"error": "CUSTOMER_NOT_FOUND", "message": "Customer report not found in credit bureau"}

# 5. WireMock health check
curl -i http://localhost:8081/__admin/health
# HTTP/1.1 200 OK
# {"status": "healthy"}
```

---

## 5. Extension Support

The `extensions/` directory includes `wiremock-jwt-extension-standalone-0.3.0.jar`, enabling JWT generation, verification, and template helpers for mTLS/OAuth2 token-based authentication scenarios when required.
