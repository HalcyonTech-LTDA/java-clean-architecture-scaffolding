# Java Clean Architecture Scaffolding

This project is structured using the principles and best practices of **Clean Architecture**, aiming for the total decoupling of core business rules from frameworks, databases, and external third-party systems.

---

## Quick Start & Scaffolding Generator (`init-project.sh`)

This repository includes an interactive shell script ([`init-project.sh`](init-project.sh)) designed to quickly bootstrap a new Gradle Java Spring Boot project fully pre-configured with this Clean Architecture scaffolding structure.

### Features of `init-project.sh`

* **Prerequisite Verification**: Checks for local `java` and `gradle` installations before execution.
* **Interactive Prompts**: Solicits project parameters with sensible defaults:
  * **Java Version**: Defaults to `26`.
  * **Project Name**: Defaults to current working directory name.
  * **Base Package**: Defaults to `br.com.<sanitized-project-name>`.
* **Automated Clean Architecture Scaffolding**:
  * Runs `gradle init` to set up the Gradle build environment.
  * Generates a comprehensive `.gitignore` configured for Java, Gradle, Maven, and IDEs.
  * Creates all Clean Architecture directory layers (`domain`, `application`, `infrastructure`) complete with descriptive `.gitkeep` files explaining each package's purpose.
* **Spring Boot & Ecosystem Configuration**:
  * Configures Gradle Version Catalog (`gradle/libs.versions.toml`) with Spring Boot, Spring Cloud, Spring Data MongoDB, Spring Batch, OpenFeign, Lombok, and JUnit 5.
  * Configures `app/build.gradle` with Java toolchains, BOM dependency management, and plugins.
  * Creates Spring Boot entry point (`App.java`) with `@SpringBootApplication` and context test (`AppTest.java`).

### Usage

Make the script executable (if needed) and execute it from the root directory of your new project:

```bash
chmod +x init-project.sh
./init-project.sh
```

Follow the on-screen prompts to customize your project configuration.

---

## Scaffolding Package Structure (`src/main`)

```text
src/main/
├── java/
│   └── com/example/demo/
│       ├── DemoApplication.java               # Spring Boot Bootstrapper (Main Class)
│       │
│       ├── domain/                            # 1. DOMAIN (Enterprise Business Rules)
│       │   ├── model/                         # Pure business entities, Value Objects, and Enums
│       │   ├── exception/                     # Domain-specific business exceptions
│       │   ├── repository/                    # Database persistence port interfaces (Outbound Ports)
│       │   └── gateway/                       # Integration interfaces for external APIs/brokers (Outbound Ports)
│       │
│       ├── application/                       # 2. APPLICATION (Application Business Rules)
│       │   ├── usecase/                       # Application boundary interfaces defining use cases (Inbound Ports)
│       │   │   └── impl/                      # Concrete use case orchestrators (Interactors)
│       │   └── dto/                           # Internal Use Case request/response DTOs
│       │
│       └── infrastructure/                    # 3. INFRASTRUCTURE (Adapters & Frameworks)
│           ├── config/                        # Spring context configuration (Manual Bean instantiation and DI)
│           ├── api/                           # HTTP REST API incoming controllers (Inbound Adapters)
│           │   ├── request/                   # Incoming HTTP DTOs (@JsonProperty, Bean validation)
│           │   ├── response/                  # Outgoing HTTP response payloads
│           │   └── mapper/                    # Mappers between HTTP requests and internal Application DTOs
│           │
│           ├── persistence/                   # Database persistence engine (Outbound Adapters)
│           │   ├── entity/                    # Physical Database/ORM Entities (JPA @Entity, MongoDB @Document, etc.)
│           │   ├── repository/                # Framework-specific repositories (MongoRepository, JpaRepository)
│           │   ├── adapter/                   # Implementation classes for domain.repository ports
│           │   └── mapper/                    # Mappers between Domain Models and Persistence Entities
│           │
│           ├── webclient/                     # External HTTP integrations (Outbound HTTP Adapters)
│           │   ├── adapter/                   # Concrete implementations of external domain.gateway HTTP clients
│           │   ├── mapper/                    # Mappers between HTTP Client models and Domain/Application objects
│           │   └── clientN/                   # Isolated client integration for a specific partner/service 'clientN'
│           │       ├── request/               # Specific request DTOs sent to external service 'clientN'
│           │       └── response/              # Specific response DTOs received from external service 'clientN'
│           │
│           ├── messaging/                     # Event Driven / Asynchronous Messaging (Outbound & Inbound Adapters)
│           │   ├── consumer/                  # Inbound message consumption (Listeners located at consumer root)
│           │   │   ├── dto/                   # DTOs representing incoming raw payload structures
│           │   │   ├── mapper/                # Mappers between raw messaging DTOs and internal Commands/DTOs
│           │   │   └── handler/               # Message processors that orchestrate and call Application Use Cases
│           │   └── producer/                  # Outbound event publishing (Publishers/Emitters)
│           │       ├── dto/                   # OutTOs representing outgoing raw payload structures
│           │       ├── mapper/                # Mappers between internal models/DTOs and outgoing messaging DTOs
│           │       └── adapter/               # Concrete broker integration adapter (e.g., classes using KafkaTemplate)
│           │
│           ├── schedule/                      # Scheduled tasks running periodically (cron/time-triggered tasks)
│           │
│           └── batch/                         # Batch processing integration (Spring Batch)
│               ├── config/                    # Configuration of Spring Batch Jobs and Steps
│               ├── reader/                    # Spring Batch ItemReaders
│               ├── writer/                    # Spring Batch ItemWriters
│               └── processor/                 # Spring Batch ItemProcessors
│
└── resources/
    ├── application.yml                        # Global configurations and active Spring profile selection
    └── application-dev.yml                    # Configuration for the 'dev' profile (e.g., local database settings)
```

---

## Detailed Layer Description

### 1. Domain Layer (`domain`)
The heart of the application. It has zero dependencies on external frameworks, databases, or runtime libraries (e.g., Spring Boot, JPA, etc.).
* **`model`**: Houses rich business entities, value objects, records, and enums with business invariants and validation.
* **`exception`**: Holds domain exceptions representing violations of business logic.
* **`repository`**: Defines outbound database ports (interfaces) specifying persistence capabilities required by the domain.
* **`gateway`**: Defines outbound gateway ports (interfaces) specifying external integrations (e.g., sending emails or publishing events) required by the domain.

### 2. Application Layer (`application`)
Orchestrates application control flow, translating external requests into domain actions.
* **`usecase`**: Declares inbound ports (interfaces) specifying operations supported by the system.
* **`usecase/impl`**: Implements use case interfaces (interactors). They call domain business entities and orchestrate outbound interfaces (repositories and gateways) via dependency injection.
* **`dto`**: Pure data transfer objects to exchange input/output parameters with the application layer without exposing raw database entities or rich domain models.

### 3. Infrastructure Layer (`infrastructure`)
The outermost boundary. Houses concrete frameworks, databases, adapters, and tools.
* **`config`**: Holds Spring configuration classes and manual bean factories to construct application use cases, keeping use case interactors free of framework annotations (like `@Service`).
* **`api`**: REST/HTTP incoming endpoints. They receive requests, run bean validation, map JSON to internal application DTOs, and trigger use cases.
* **`persistence`**: Database implementation logic. Houses the database drivers, Spring Data repositories, ORM entity configurations, and adapters implementing domain repositories.
* **`webclient`**: Houses concrete HTTP clients (OpenFeign, Spring WebClient, etc.) to consume third-party services like `clientN`.
* **`messaging`**: Handles event-driven integrations. Listeners in the `consumer` root receive payloads, which are transformed via `mapper` and passed to `handler` processors. Outbound publishers are implemented in the `producer` subpackage.
* **`schedule`**: Houses background tasks running periodically via Spring `@Scheduled` cron expressions.
* **`batch`**: Coordinates chunk-oriented batch logic using Spring Batch (jobs, steps, readers, writers, and processors).
