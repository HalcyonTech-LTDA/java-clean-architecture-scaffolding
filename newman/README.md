# Automated API Testing with Newman

This directory contains the automated end-to-end API test suite for the microservice, built with [Newman](https://github.com/postmanlabs/newman) (the Postman command-line collection runner).

---

## 1. Directory Structure

```text
newman/
├── README.md                                    # API testing guide and runbook
├── collections/
│   └── ms-template-java.postman_collection.json # Complete API collection with tests & assertions
├── environments/
│   ├── local.postman_environment.json           # Target: http://localhost:8080
│   ├── dev.postman_environment.json             # Target: https://dev-api.example.com
│   └── staging.postman_environment.json         # Target: https://staging-api.example.com
└── reports/
    └── .gitkeep                                 # Output destination for JSON and HTML test reports
```

---

## 2. Test Suite & Coverage

The Postman collection is organized into three logical modules:

### 01 - Actuator & System
- **Actuator Health Check**: Calls `GET /actuator/health`, asserts HTTP `200 OK`, validates `{ "status": "UP" }`, and checks latency SLA (< 300ms).
- **OpenAPI Documentation**: Calls `GET /v3/api-docs`, asserts HTTP `200 OK`, and verifies OpenAPI 3.x schema availability.

### 02 - Customer Management ([`CustomerController`](../app/src/main/java/com/example/templatejava/customer/infrastructure/web/CustomerController.java))
- **Create Customer (Positive)**:
  - Dynamically generates unique customer names and emails via pre-request scripts.
  - Asserts HTTP `201 Created` and verifies presence of the `Location` header.
  - Validates response schema and persists `customerId` in the environment for subsequent tests.
- **Get Customer By ID (Positive)**:
  - Fetches the newly created customer using `{{customerId}}`.
  - Asserts HTTP `200 OK` and verifies data parity against the registration payload.
- **Get Customer - Not Found (Negative)**:
  - Requests a non-existent customer ID.
  - Asserts HTTP `404 Not Found` and validates the error problem details structure.
- **Create Customer - Blank Name (Negative)**:
  - Submits an invalid customer with whitespace-only name.
  - Asserts HTTP `400 Bad Request` or `422 Unprocessable Entity`.

### 03 - Order Management ([`OrderController`](../app/src/main/java/com/example/templatejava/order/infrastructure/web/OrderController.java))
- **Create Order (Business Flow)**:
  - Places an order using the previously created `{{customerId}}`.
  - Asserts HTTP `201 Created` (if customer has been enriched and is active) or `422 Unprocessable Entity` (if customer is still pending credit bureau evaluation).
  - Captures `orderId` if created.
- **Get Order - Not Found (Negative)**:
  - Requests a non-existent order ID.
  - Asserts HTTP `404 Not Found`.
- **Create Order - Negative Amount (Negative)**:
  - Submits an order with a negative amount (`-50.00`).
  - Asserts validation failure (`400` or `422`).

---

## 3. Environment Selection

Select target environments using the `ENV` variable:

| Environment | Base URL | Config File |
| :--- | :--- | :--- |
| **`local`** *(default)* | `http://localhost:8080` | `newman/environments/local.postman_environment.json` |
| **`dev`** | `https://dev-api.example.com` | `newman/environments/dev.postman_environment.json` |
| **`staging`** | `https://staging-api.example.com` | `newman/environments/staging.postman_environment.json` |

---

## 4. Execution via Taskfile

The root [`Taskfile.yaml`](../Taskfile.yaml) features automated native execution with an intelligent Docker fallback (`postman/newman:alpine`):

```bash
# Execute against local application (default)
task test:api

# Execute against specific remote environment
task test:api ENV=dev
task test:api ENV=staging
```

---

## 5. Direct CLI Execution

If you have Newman installed locally via npm/nvm:

```bash
# Basic run with CLI reporter
newman run newman/collections/ms-template-java.postman_collection.json \
  -e newman/environments/local.postman_environment.json

# Run with JSON report export
newman run newman/collections/ms-template-java.postman_collection.json \
  -e newman/environments/local.postman_environment.json \
  --reporters cli,json \
  --reporter-json-export newman/reports/report-local.json
```

---

## 6. Standalone Docker Execution

Run Newman without installing Node.js or npm on your host:

```bash
docker run --rm -i \
  --network host \
  -v "$(pwd):/etc/newman" \
  postman/newman:alpine run "/etc/newman/newman/collections/ms-template-java.postman_collection.json" \
  -e "/etc/newman/newman/environments/local.postman_environment.json" \
  --reporters cli
```
