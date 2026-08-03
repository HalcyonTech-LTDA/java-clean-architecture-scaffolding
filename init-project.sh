#!/usr/bin/env bash
set -e

# 1. Verify Prerequisites
if ! command -v gradle &> /dev/null; then
    echo "Error: Gradle is not installed or not in PATH." >&2
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "Error: Java SDK is not installed or not in PATH." >&2
    exit 1
fi

# 2. Interactive Prompts with Defaults
DEFAULT_PROJECT_NAME="$(basename "$PWD")"
DEFAULT_JAVA_VERSION="26"

read -p "Java version [$DEFAULT_JAVA_VERSION]: " JAVA_VERSION
JAVA_VERSION="${JAVA_VERSION:-$DEFAULT_JAVA_VERSION}"

read -p "Project name [$DEFAULT_PROJECT_NAME]: " PROJECT_NAME
PROJECT_NAME="${PROJECT_NAME:-$DEFAULT_PROJECT_NAME}"

SANITIZED_PROJECT_NAME="$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr '-' '.')"
DEFAULT_BASE_PACKAGE="br.com.${SANITIZED_PROJECT_NAME}"

read -p "Base package [$DEFAULT_BASE_PACKAGE]: " BASE_PACKAGE
BASE_PACKAGE="${BASE_PACKAGE:-$DEFAULT_BASE_PACKAGE}"

echo "--> Initializing Gradle project..."
gradle init \
  --type java-application \
  --dsl groovy \
  --test-framework junit-jupiter \
  --project-name "$PROJECT_NAME" \
  --package "$BASE_PACKAGE" \
  --java-version "$JAVA_VERSION" \
  --no-split-project \
  --use-defaults \
  --overwrite

echo "--> Generating .gitignore..."
cat << 'EOF' > .gitignore
# Created by https://www.toptal.com/developers/gitignore/api/java,gradle,maven,visualstudiocode
# Edit at https://www.toptal.com/developers/gitignore?templates=java,gradle,maven,visualstudiocode

### Java ###
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
hs_err_pid*
replay_pid*

### Maven ###
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
# https://github.com/takari/maven-wrapper#usage-without-binary-jar
.mvn/wrapper/maven-wrapper.jar

# Eclipse m2e generated files
# Eclipse Core
.project
# JDT-specific (Eclipse Java Development Tools)
.classpath

### VisualStudioCode ###
.vscode/*

# Local History for Visual Studio Code
.history/

# Built Visual Studio Code Extensions
*.vsix

### VisualStudioCode Patch ###
# Ignore all local history of files
.history
.ionide

### Gradle ###
.gradle
**/build/
!src/**/build/

# Ignore Gradle GUI config
gradle-app.setting

# Avoid ignoring Gradle wrapper jar file (.jar files are usually ignored)
!gradle-wrapper.jar

# Avoid ignore Gradle wrappper properties
!gradle-wrapper.properties

# Cache of project
.gradletasknamecache

# Eclipse Gradle plugin generated files
# Eclipse Core
# JDT-specific (Eclipse Java Development Tools)

### Gradle Patch ###
# Java heap dump
*.hprof

# End of https://www.toptal.com/developers/gitignore/api/java,gradle,maven,visualstudiocode
# Ignore Gradle build output directory
build

# Ignore Kotlin plugin data
.kotlin
EOF

echo "--> Creating Clean Architecture folder structure with English .gitkeep files..."
PACKAGE_PATH=$(echo "$BASE_PACKAGE" | tr '.' '/')
BASE_DIR="app/src/main/java/$PACKAGE_PATH"

create_gitkeep() {
    local dir="$1"
    local content="$2"
    mkdir -p "$dir"
    echo "$content" > "$dir/.gitkeep"
}

# Domain Layer
create_gitkeep "$BASE_DIR/domain/model" \
"Contains pure business entities, Value Objects, and Enums of the domain. These classes represent core business concepts and rules, completely independent of external frameworks and technologies."

create_gitkeep "$BASE_DIR/domain/exception" \
"Contains domain-specific business exceptions (e.g., OrderNotFoundException). These exceptions are thrown by domain entities or use cases to indicate business rule violations."

create_gitkeep "$BASE_DIR/domain/repository" \
"Contains domain repository interfaces (outbound ports). They define the persistence contracts required by the domain. Concrete implementations reside in the infrastructure layer (persistence)."

create_gitkeep "$BASE_DIR/domain/gateway" \
"Contains domain gateway interfaces (outbound ports). They define contracts for communication with external services, third-party APIs, or messaging systems. Database persistence contracts remain in the repository folder."

# Application Layer
create_gitkeep "$BASE_DIR/application/usecase" \
"Contains interfaces defining application-specific rules (use cases / inbound ports). They declare the business actions executed by the application. Concrete implementations are located in the impl subpackage."

create_gitkeep "$BASE_DIR/application/usecase/impl" \
"Contains concrete implementations of the declared use cases. They coordinate data flow to and from domain entities and utilize repository interfaces for persistence."

create_gitkeep "$BASE_DIR/application/dto" \
"Contains internal DTOs (Data Transfer Objects) serving as input requests/commands and output responses/queries for use cases, isolating domain models from external presentation structures."

# Infrastructure Layer - Config
create_gitkeep "$BASE_DIR/infrastructure/config" \
"Contains Spring Framework configuration classes (such as dependency injection and Beans). Responsible for instantiating application use cases and injecting their respective adapter implementations."

# Infrastructure Layer - API
create_gitkeep "$BASE_DIR/infrastructure/api" \
"Contains REST API controllers (inbound adapters). They expose HTTP endpoints, handle external requests, and delegate execution to application use cases."

create_gitkeep "$BASE_DIR/infrastructure/api/request" \
"Contains API-specific incoming request DTOs. They feature data validation annotations (Bean Validation) and JSON serialization/deserialization bindings for external communications."

create_gitkeep "$BASE_DIR/infrastructure/api/response" \
"Contains API-specific outgoing response DTOs. They contain serialization annotations and are returned by controllers to external clients."

create_gitkeep "$BASE_DIR/infrastructure/api/mapper" \
"Contains mappers (e.g., MapStruct interfaces) responsible for translating external API request DTOs to internal application DTOs/commands."

# Infrastructure Layer - Persistence
create_gitkeep "$BASE_DIR/infrastructure/persistence/entity" \
"Contains physical database entities (e.g., MongoDB documents with @Document, JPA entities with @Entity) directly mapping database tables or collections."

create_gitkeep "$BASE_DIR/infrastructure/persistence/repository" \
"Contains framework persistence interfaces (e.g., Spring Data MongoRepository or JpaRepository) providing native CRUD operations."

create_gitkeep "$BASE_DIR/infrastructure/persistence/adapter" \
"Contains persistence adapter implementations (outbound adapters) for domain repository interfaces (domain.repository), executing data persistence operations and domain mapping."

create_gitkeep "$BASE_DIR/infrastructure/persistence/mapper" \
"Contains mappers responsible for bidirectional conversion between domain entities (domain.model) and physical database persistence entities."

# Infrastructure Layer - WebClient
create_gitkeep "$BASE_DIR/infrastructure/webclient/adapter" \
"Contains concrete HTTP adapter implementations of domain gateway interfaces (domain.gateway), performing HTTP calls to third-party services and APIs."

create_gitkeep "$BASE_DIR/infrastructure/webclient/mapper" \
"Contains mappers translating internal application DTOs and domain entities to and from HTTP client request/response models."

create_gitkeep "$BASE_DIR/infrastructure/webclient/clientN" \
"Contains the HTTP client (such as OpenFeign interface or WebClient implementation) for integrating with partner service 'clientN'."

create_gitkeep "$BASE_DIR/infrastructure/webclient/clientN/request" \
"Contains request payload DTOs sent to external third-party API service 'clientN'."

create_gitkeep "$BASE_DIR/infrastructure/webclient/clientN/response" \
"Contains response payload DTOs received from external third-party API service 'clientN'."

# Infrastructure Layer - Messaging
create_gitkeep "$BASE_DIR/infrastructure/messaging/consumer" \
"Contains asynchronous message listeners/consumers listening on external queues or topics and forwarding payloads to handlers."

create_gitkeep "$BASE_DIR/infrastructure/messaging/consumer/dto" \
"Contains DTOs representing the raw physical structure of incoming messages/events consumed from messaging queues or topics (e.g., Kafka/RabbitMQ)."

create_gitkeep "$BASE_DIR/infrastructure/messaging/consumer/handler" \
"Contains async message/event handlers that process consumed message payloads and delegate execution to application use cases."

create_gitkeep "$BASE_DIR/infrastructure/messaging/consumer/mapper" \
"Contains mappers converting raw consumed message DTOs to internal application use case command DTOs."

create_gitkeep "$BASE_DIR/infrastructure/messaging/producer" \
"Contains asynchronous event/message publishers responsible for emitting domain notifications or state changes to external message queues."

create_gitkeep "$BASE_DIR/infrastructure/messaging/producer/dto" \
"Contains DTOs representing outbound message/event payload structures published to external message brokers."

create_gitkeep "$BASE_DIR/infrastructure/messaging/producer/adapter" \
"Contains outbound event producer adapter implementations (e.g., KafkaTemplate or RabbitTemplate) implementing domain messaging gateway ports."

create_gitkeep "$BASE_DIR/infrastructure/messaging/producer/mapper" \
"Contains mappers converting domain entities or internal application DTOs into outbound message DTOs."

# Infrastructure Layer - Schedule
create_gitkeep "$BASE_DIR/infrastructure/schedule" \
"Contains scheduled tasks executed periodically or at fixed time intervals (e.g., Spring @Scheduled cron jobs)."

# Infrastructure Layer - Batch
create_gitkeep "$BASE_DIR/infrastructure/batch" \
"Contains Spring Batch components and configuration for batch processing jobs."

create_gitkeep "$BASE_DIR/infrastructure/batch/config" \
"Contains configuration classes for Spring Batch Jobs and Steps, orchestrating batch processing execution flow."

create_gitkeep "$BASE_DIR/infrastructure/batch/reader" \
"Contains ItemReader components for reading input data chunks in Spring Batch steps."

create_gitkeep "$BASE_DIR/infrastructure/batch/processor" \
"Contains ItemProcessor components for transforming or validating items during Spring Batch step processing."

create_gitkeep "$BASE_DIR/infrastructure/batch/writer" \
"Contains ItemWriter components for persisting processed batch items in Spring Batch steps."

echo "--> Enriching gradle/libs.versions.toml..."
mkdir -p gradle
cat << 'EOF' > gradle/libs.versions.toml
[versions]
guava = "33.5.0-jre"
springBoot = "4.1.0"
springDependencyManagement = "1.1.7"
springCloud = "2025.1.2"
junitJupiter = "6.0.1"
commonsCollections4 = "4.5.0"
commonsLang3 = "3.20.0"

[libraries]
guava = { module = "com.google.guava:guava", version.ref = "guava" }

# Spring Boot Starters
spring-boot-starter-batch = { module = "org.springframework.boot:spring-boot-starter-batch" }
spring-boot-starter-batch-data-mongodb = { module = "org.springframework.boot:spring-boot-starter-batch-data-mongodb" }
spring-boot-starter-data-mongodb = { module = "org.springframework.boot:spring-boot-starter-data-mongodb" }
spring-boot-starter-webmvc = { module = "org.springframework.boot:spring-boot-starter-webmvc" }

# Spring Cloud
spring-cloud-starter-openfeign = { module = "org.springframework.cloud:spring-cloud-starter-openfeign" }
spring-cloud-dependencies = { module = "org.springframework.cloud:spring-cloud-dependencies", version.ref = "springCloud" }

# Lombok
lombok = { module = "org.projectlombok:lombok" }

# Test dependencies
spring-boot-starter-batch-data-mongodb-test = { module = "org.springframework.boot:spring-boot-starter-batch-data-mongodb-test" }
spring-boot-starter-batch-test = { module = "org.springframework.boot:spring-boot-starter-batch-test" }
spring-boot-starter-data-mongodb-test = { module = "org.springframework.boot:spring-boot-starter-data-mongodb-test" }
spring-boot-starter-webmvc-test = { module = "org.springframework.boot:spring-boot-starter-webmvc-test" }
junit-platform-launcher = { module = "org.junit.platform:junit-platform-launcher" }

# Commons
commons-collections4 = { module = "org.apache.commons:commons-collections4", version.ref = "commonsCollections4" }
commons-lang3 = { module = "org.apache.commons:commons-lang3", version.ref = "commonsLang3" }

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "springBoot" }
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "springDependencyManagement" }
EOF

echo "--> Enriching app/build.gradle..."
cat << EOF > app/build.gradle
plugins {
    id 'application'
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = '${BASE_PACKAGE}'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation libs.spring.boot.starter.batch
    implementation libs.spring.boot.starter.batch.data.mongodb
    implementation libs.spring.boot.starter.data.mongodb
    implementation libs.spring.boot.starter.webmvc
    implementation libs.spring.cloud.starter.openfeign
    compileOnly libs.lombok
    annotationProcessor libs.lombok
    testImplementation libs.spring.boot.starter.batch.data.mongodb.test
    testImplementation libs.spring.boot.starter.batch.test
    testImplementation libs.spring.boot.starter.data.mongodb.test
    testImplementation libs.spring.boot.starter.webmvc.test
    testCompileOnly libs.lombok
    testRuntimeOnly libs.junit.platform.launcher
    testAnnotationProcessor libs.lombok
    implementation libs.commons.collections4
    implementation libs.commons.lang3
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:\${libs.versions.springCloud.get()}"
    }
}

tasks.named('test') {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(${JAVA_VERSION})
    }
}

application {
    mainClass = '${BASE_PACKAGE}.App'
}
EOF

echo "--> Enriching App.java and AppTest.java with Spring Boot annotations..."
mkdir -p "app/src/main/java/$PACKAGE_PATH"
cat << EOF > "app/src/main/java/$PACKAGE_PATH/App.java"
package ${BASE_PACKAGE};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
EOF

mkdir -p "app/src/test/java/$PACKAGE_PATH"
cat << EOF > "app/src/test/java/$PACKAGE_PATH/AppTest.java"
package ${BASE_PACKAGE};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppTest {
    @Test
    void contextLoads() {
    }
}
EOF

echo "--> Setup completed successfully!"
