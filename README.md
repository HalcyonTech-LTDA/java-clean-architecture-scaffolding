# Corporate Golden Path Microservice Template

> Production-grade Clean Architecture microservice template built on **Java 26**, **Spring Boot 4.1.1**, and **Gradle (Kotlin DSL)**.

---

## Architecture Overview

This template implements **Clean Architecture** (Hexagonal / Screaming Architecture) organized by **domain features** (`customer`, `order`, `common`).

```text
app/src/main/java/com/example/templatejava/
├── ApiApplication.java               # Bootstrapper for REST HTTP API profile
├── SchedulingApplication.java        # Bootstrapper for scheduled background jobs
│
├── customer/                         # Customer Feature Slice
│   ├── domain/                       # Pure Domain: Models, Invariants, Repository Ports
│   ├── application/                  # Use Cases (Inbound Ports & Interactors), Gateways, Facades
│   └── infrastructure/               # Web Controllers, MongoDB Adapters, Feign Clients, Jobs
│
├── order/                            # Order Feature Slice
│   ├── domain/                       # Pure Domain: Order Entity, Business Exceptions, Repository Ports
│   ├── application/                  # Use Cases: CreateOrder, ExpireOrders, FindOrderById
│   └── infrastructure/               # REST Endpoints, MongoDB Persistence, Anti-Corruption Layer (ACL)
│
└── common/                           # Cross-cutting Infrastructure
    └── infrastructure/               # Feign configs, Global Exception Handling, Time, Scheduling, OpenAPI
```

---

## Tech Stack & Tooling

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (Virtual Threads, Records, Pattern Matching) | 26 |
| **Framework** | Spring Boot | 4.1.1 |
| **Build System** | Gradle (Kotlin DSL, `build-logic` conventions, version catalog) | 9.5.1 |
| **Database** | MongoDB (Spring Data Reactive / Imperative) | 7.x |
| **Observability** | OpenTelemetry, Micrometer Tracing, Prometheus | Standard |
| **Code Quality** | Spotless, ArchUnit, JaCoCo (Coverage verification) | Latest |
| **Security SCA/SAST** | CycloneDX SBOM, Trivy, Gitleaks, Semgrep | Latest |
| **Task Automation** | Taskfile (`task`) | 3.x |
| **Git Hooks & Release** | Lefthook, Commitlint, Semantic Release (in `tools/release/`) | Latest |

---

## Quick Start & Local Execution

### Prerequisites
- **JDK 26** (e.g., Eclipse Temurin via SDKMAN: `sdk install java 26.0.2-tem`)
- **Docker** & **Docker Compose**
- **Taskfile CLI** (`brew install go-task`)

### 1. Initialize Git Hooks
```bash
task setup:hooks
```

### 2. Start Supporting Infrastructure
```bash
docker compose up -d mongodb wiremock
```

### 3. Run Applications
- **Web API**:
  ```bash
  task run:api
  ```
  Accessible at `http://localhost:8080/swagger-ui.html`
- **Scheduling Worker**:
  ```bash
  task run:scheduling
  ```

---

## Quality Gates & Verification

Run the full local verification pipeline:
```bash
./gradlew check
```
This executes:
- **Code Formatting**: Spotless check & apply (`./gradlew spotlessApply`)
- **Unit & Integration Tests**: JUnit Jupiter, AssertJ, Testcontainers
- **Architectural Rules**: ArchUnit Clean Architecture boundaries
- **Coverage Verification**: JaCoCo coverage thresholds
- **SBOM Generation**: CycloneDX bill of materials (`app/build/reports/cyclonedx-direct/bom.json`)

### Security Scanning
```bash
task security:gitleaks      # Secret detection
task security:sast          # Semgrep static analysis
task security:sbom:scan     # Trivy SCA scan of CycloneDX SBOM
```

---

## Git Hooks & Release Tooling

All Node.js tooling for repository governance, commit validation, and automated semantic releases is isolated in `tools/release/` to keep the root directory strictly focused on Java:

- **Lefthook**: Configured in [`lefthook.yml`](lefthook.yml) (pre-commit Gitleaks & Spotless, commit-msg Commitlint).
- **Commitlint**: Configured in [`tools/release/commitlint.config.js`](tools/release/commitlint.config.js) extending `@commitlint/config-conventional`.
- **Semantic Release**: Configured in [`tools/release/.releaserc.json`](tools/release/.releaserc.json) for automated GitHub releases and SBOM attachments.

For further details, see the [Release Tooling Documentation](tools/release/README.md).
