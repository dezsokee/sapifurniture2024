# Furniture Backend API

A Spring Boot REST API for furniture body management and cutting optimization.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Building the Application](#building-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)

## Overview

The Furniture Backend API provides functionality for:
- Managing furniture body elements (CRUD operations)
- Optimizing cutting plans for furniture elements on cutting sheets
- Minimizing material waste through intelligent bin packing algorithms

## Technology Stack

- **Framework:** Spring Boot 2.7.4
- **Java Version:** 11
- **Build Tool:** Maven
- **Database:** PostgreSQL 16
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security
- **Testing:** JUnit 5, Cucumber, Testcontainers
- **Additional:** Spring Batch, Spring WebFlux, Elasticsearch

### Key Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

## Project Architecture

The application follows a layered architecture pattern:

```
be/
├── src/
│   ├── main/
│   │   ├── java/ro/sapientia/furniture/
│   │   │   ├── FurnitureApplication.java       # Main application entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfiguration.java  # Security configuration
│   │   │   ├── controller/                     # REST API endpoints
│   │   │   │   └── FurnitureController.java
│   │   │   ├── service/                        # Business logic layer
│   │   │   │   ├── FurnitureBodyService.java
│   │   │   │   └── CutOptimizationService.java
│   │   │   ├── repository/                     # Data access layer
│   │   │   │   └── FurnitureBodyRepository.java
│   │   │   ├── model/
│   │   │   │   ├── entities/                   # JPA entities
│   │   │   │   │   ├── FurnitureBody.java
│   │   │   │   │   ├── PlacedElement.java
│   │   │   │   │   ├── CutRequest.java
│   │   │   │   │   └── CutResponse.java
│   │   │   │   └── dto/                        # Data Transfer Objects
│   │   │   │       ├── FurnitureBodyDTO.java
│   │   │   │       ├── PlacedElementDTO.java
│   │   │   │       ├── CutRequestDTO.java
│   │   │   │       └── CutResponseDTO.java
│   │   │   ├── mapper/                         # Entity-DTO mappers
│   │   │   │   ├── FurnitureBodyMapper.java
│   │   │   │   ├── PlacedElementMapper.java
│   │   │   │   └── CutMapper.java
│   │   │   └── exception/                      # Exception handling
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── CutOptimizationException.java
│   │   │       └── ErrorResponse.java
│   │   └── resources/
│   │       └── application.properties          # Application configuration
│   ├── test/                                   # Unit tests
│   └── eetest/                                 # Integration & BDD tests
├── pom.xml                                     # Maven configuration
└── Dockerfile                                  # Docker build configuration
```

### Architecture Layers

1. **Controller Layer**: Handles HTTP requests and responses
   - `FurnitureController` - REST endpoints for furniture operations

2. **Service Layer**: Contains business logic
   - `FurnitureBodyService` - CRUD operations for furniture bodies
   - `CutOptimizationService` - Cutting optimization algorithm (FFDH)

3. **Repository Layer**: Data access using JPA
   - `FurnitureBodyRepository` - Database operations for furniture bodies

4. **Model Layer**:
   - **Entities**: JPA entities mapped to database tables
   - **DTOs**: Data transfer objects for API communication
   - **Mappers**: Convert between entities and DTOs

5. **Exception Handling**: Centralized error handling
   - `GlobalExceptionHandler` - Handles all exceptions with appropriate HTTP responses

### Design Patterns

- **Dependency Injection**: Constructor-based DI for loose coupling
- **DTO Pattern**: Separation of internal entities from API contracts
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **Exception Handling Pattern**: Centralized error handling with @ControllerAdvice

## Database Schema

### Tables

#### furniture_body

Stores furniture element information.

```sql
CREATE TABLE furniture_body (
    id BIGINT PRIMARY KEY,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    depth INTEGER NOT NULL
);

-- Sequence for ID generation
CREATE SEQUENCE pk_furniture_body;
```

### Entity Relationship

```
furniture_body
├── id (PK) - BIGINT - Auto-generated sequence
├── width - INTEGER - Element width in mm
├── height - INTEGER - Element height in mm
└── depth - INTEGER - Element depth in mm
```

### Database Configuration

The application uses PostgreSQL with the following default configuration:

- **Database Name**: `furniture`
- **Username**: `sapi`
- **Password**: `sapi`
- **Port**: `5432`
- **Schema**: Auto-created by Hibernate
- **DDL Mode**: `update` (auto-updates schema based on entities)

## Installation

### Prerequisites

- **Java 11** or higher
- **Maven 3.6+**
- **PostgreSQL 16** (or use Docker)
- **Git**

### Clone the Repository

```bash
git clone <repository-url>
cd deploy_furniture_project/be
```

### Set Up Database

#### Option 1: Local PostgreSQL

1. Install PostgreSQL
2. Create database and user:

```sql
CREATE DATABASE furniture;
CREATE USER sapi WITH PASSWORD 'sapi';
GRANT ALL PRIVILEGES ON DATABASE furniture TO sapi;
```

#### Option 2: Docker PostgreSQL

```bash
docker run -d \
  --name furniture_postgres \
  -e POSTGRES_DB=furniture \
  -e POSTGRES_USER=sapi \
  -e POSTGRES_PASSWORD=sapi \
  -p 5432:5432 \
  postgres:16
```

## Running the Application

### Using Maven

```bash
# From the be/ directory
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### Using Java JAR

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/furniture-0.0.1-SNAPSHOT.jar
```

### Configuration Options

You can override default configurations using environment variables or command-line arguments:

```bash
# Using environment variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/furniture
export SPRING_DATASOURCE_USERNAME=sapi
export SPRING_DATASOURCE_PASSWORD=sapi
mvn spring-boot:run

# Using command-line arguments
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080 --spring.datasource.url=jdbc:postgresql://localhost:5432/furniture"
```

## Building the Application

### Maven Build

```bash
# Clean and build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Package only
mvn package
```

### Build Outputs

- **JAR file**: `target/furniture-0.0.1-SNAPSHOT.jar`
- **Compiled classes**: `target/classes/`
- **Test results**: `target/surefire-reports/`

## API Documentation

See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for detailed API reference including:
- All available endpoints
- Request/response formats
- Example calls
- Error codes and handling

### Quick API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/furniture/all` | Get all furniture bodies |
| GET | `/furniture/find/{id}` | Get furniture body by ID |
| POST | `/furniture/add` | Create new furniture body |
| POST | `/furniture/update` | Update existing furniture body |
| GET | `/furniture/delete/{id}` | Delete furniture body |
| POST | `/furniture/cut` | Optimize cutting plan |

## Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Categories

```bash
# Unit tests only
mvn test -Dtest="*Test"

# Integration tests
mvn test -Dtest="*IntegrationTest"

# BDD tests
mvn test -Dtest="*BDDTest"
```

### Test Structure

```
src/
├── test/                    # Unit tests
│   └── java/ro/sapientia/furniture/
│       ├── controller/
│       ├── service/
│       └── repository/
└── eetest/                  # Integration & E2E tests
    ├── java/ro/sapientia/furniture/
    │   └── bdt/            # Cucumber BDD tests
    └── resources/
        ├── cucumber.properties
        └── eetest.properties
```

### Test Coverage

- **Unit Tests**: Service layer, mappers, entities
- **Integration Tests**: Repository layer with Testcontainers
- **BDD Tests**: Component and end-to-end scenarios with Cucumber
- **Controller Tests**: REST API endpoint testing

## Docker Deployment

### Build Docker Image

```bash
# From project root
docker build -t furniture-backend -f be/Dockerfile .
```

### Run Docker Container

```bash
docker run -d \
  --name furniture-backend \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/furniture \
  -e SPRING_DATASOURCE_USERNAME=sapi \
  -e SPRING_DATASOURCE_PASSWORD=sapi \
  furniture-backend
```

### Docker Compose

From the project root:

```bash
# Start all services (backend, frontend, database, nginx)
docker-compose up -d

# View logs
docker-compose logs -f furniture_backend

# Stop services
docker-compose down
```

The complete application will be available at `http://localhost`

### Kubernetes Deployment (Helm)

```bash
# From project root
cd helm

# Install/upgrade the Helm chart
helm upgrade --install furniture . \
  --namespace furniture \
  --create-namespace

# Check deployment status
kubectl get pods -n furniture

# Access the application
kubectl port-forward -n furniture svc/furniture-frontend 8080:80
```

## Configuration

### Application Properties

File: `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/furniture
spring.datasource.username=sapi
spring.datasource.password=sapi
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Configuration
server.port=8081
```

### Environment-Specific Configuration

Create additional property files for different environments:

- `application-dev.properties` - Development
- `application-test.properties` - Testing
- `application-prod.properties` - Production

Activate with:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | HTTP server port | 8081 |
| `spring.datasource.url` | Database connection URL | jdbc:postgresql://localhost:5432/furniture |
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy | update |
| `spring.jpa.show-sql` | Show SQL statements in logs | true |

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running
   - Check connection URL, username, and password
   - Ensure database `furniture` exists

2. **Port Already in Use**
   - Change port in `application.properties`: `server.port=8082`
   - Or kill process using port 8081

3. **Build Failures**
   - Ensure Java 11 is installed: `java -version`
   - Clear Maven cache: `mvn clean`
   - Update dependencies: `mvn dependency:resolve`

### Logging

View application logs:

```bash
# When running with Maven
mvn spring-boot:run

# When running JAR
java -jar target/furniture-0.0.1-SNAPSHOT.jar --logging.level.ro.sapientia=DEBUG
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Add/update tests
4. Ensure all tests pass: `mvn test`
5. Submit a pull request

## License

See [LICENSE](./LICENSE) file for details.

## Contact

For questions or support, please contact the development team.

