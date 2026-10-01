# Performance & Load Testing with k6

This directory contains the enterprise-grade performance, stress, and spike testing suite for the microservice, built with [Grafana k6](https://k6.io/).

---

## 1. Directory Structure

```text
k6/
├── README.md                 # Performance testing guide and runbook
├── config/
│   └── environments.js      # Environment configuration (local, dev, staging, prod, custom TARGET_URL)
├── helpers/
│   ├── data-generator.js    # Dynamic and realistic test data generators (unique customers, orders, UUIDs)
│   └── http-client.js       # Standardized HTTP client with common headers, timeouts, and metric tags
├── scenarios/
│   ├── customer-flows.js    # Customer creation and retrieval journeys (POST /customers, GET /customers/{id})
│   └── order-flows.js       # Order creation and retrieval journeys (POST /orders, GET /orders/{id})
├── tests/
│   ├── 01-smoke-test.js     # Baseline sanity test (2 VUs, 15s) across health, customer, and order endpoints
│   ├── 02-load-test.js      # Sustained realistic traffic with ramping stages (up to 30 VUs, ~4m)
│   ├── 03-stress-test.js    # Scaling beyond standard peak to breaking point (up to 150 VUs, ~6m)
│   └── 04-spike-test.js     # Sudden, aggressive traffic surge and recovery verification (up to 120 VUs in 10s)
└── reports/                 # Output directory for HTML Web Dashboards and JSON summaries
    └── .gitkeep
```

---

## 2. Target Endpoints & Business Scenarios

The suite exercises the core application entrypoints:

### Customer Service ([`CustomerController`](../app/src/main/java/com/example/templatejava/customer/infrastructure/web/CustomerController.java))
- `POST /customers`: Creates a customer with randomized unique names and emails. Validates `201 Created` status and payload integrity.
- `GET /customers/{id}`: Fetches the created customer by ID. Validates `200 OK` status and identity.
- `GET /customers/{missing-id}`: Tests resilience against non-existent records, expecting `404 Not Found`.

### Order Service ([`OrderController`](../app/src/main/java/com/example/templatejava/order/infrastructure/web/OrderController.java))
- `POST /orders`: Places an order for the registered customer. Validates valid domain outcomes (`201 Created` if eligible or `422 Unprocessable Entity` if pending bureau enrichment).
- `GET /orders/{id}`: Fetches orders by ID. Validates `200 OK`.
- `GET /orders/{missing-id}`: Tests resilience against non-existent orders, expecting `404 Not Found`.

---

## 3. Environment Selection (`K6_ENV`)

You can switch the target environment dynamically by passing `K6_ENV` (or overriding directly with `TARGET_URL`):

| Environment | Base URL | Usage |
| :--- | :--- | :--- |
| **`local`** *(default)* | `http://localhost:8080` (or `http://host.docker.internal:8080` in Docker) | Local development |
| **`dev`** | `https://dev-api.example.com` | Development cluster |
| **`staging`** | `https://staging-api.example.com` | Pre-production testing |
| **`prod`** | `https://api.example.com` | Production verification / dark launch |

---

## 4. Test Types & Execution via Taskfile

The root [`Taskfile.yaml`](../Taskfile.yaml) provides automated tasks featuring an intelligent Docker fallback. If the `k6` CLI is not installed locally, tests run seamlessly inside the official `grafana/k6` container.

### Smoke Test (Baseline Sanity)
Verifies that all endpoints are operational and respond within acceptable latency.
```bash
# Local environment
task k6:smoke

# Specific environment
task k6:smoke K6_ENV=dev
```

### Load Test (Expected Peak Traffic)
Simulates normal and peak daily traffic patterns with realistic think time and ramp-up/ramp-down stages.
```bash
task k6:load
task k6:load K6_ENV=staging
```

### Stress Test (Breaking Point Discovery)
Progressively increases concurrent virtual users (10 -> 50 -> 100 -> 150 VUs) to test connection limits, thread pool saturation, and database contention.
```bash
task k6:stress
task k6:stress K6_ENV=staging
```

### Spike Test (Surge Traffic & Recovery)
Simulates sudden traffic spikes (e.g., flash sales or marketing campaigns) surging from 5 to 120 VUs in 10 seconds, and measures system recovery once traffic drops back.
```bash
task k6:spike
```

---

## 5. Direct CLI Execution

If you have the `k6` CLI installed locally:

```bash
# Run smoke test targeting local instance
k6 run k6/tests/01-smoke-test.js

# Run with Web Dashboard enabled
K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_EXPORT=k6/reports/01-smoke-test.html k6 run k6/tests/01-smoke-test.js

# Run with custom target URL and environment
k6 run -e K6_ENV=dev -e TARGET_URL=https://my-app.internal:8080 k6/tests/02-load-test.js
```

---

## 6. HTML Reports & Web Dashboard

All Taskfile tasks export an interactive HTML Web Dashboard to `k6/reports/` using `K6_WEB_DASHBOARD_EXPORT`:
- `k6/reports/01-smoke-test.html`
- `k6/reports/02-load-test.html`
- `k6/reports/03-stress-test.html`
- `k6/reports/04-spike-test.html`

Open any of these files in your browser to view real-time latency graphs, throughput percentiles (p90, p95, p99), and threshold compliance.
