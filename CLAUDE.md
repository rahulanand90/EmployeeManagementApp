# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Spring Boot Employee Management System (v3.2.0, Java 21) - A comprehensive REST API demonstrating modern Spring Boot patterns with JPA, validation, and OpenAPI documentation.

## Essential Commands

### Local Development (H2 Database)
```bash
mvn clean install          # Clean build and test
mvn spring-boot:run       # Run application (port 8080)
mvn test                  # Run tests
mvn package               # Create JAR file
```

### Docker Development (PostgreSQL)
```bash
# Build and start all services (PostgreSQL + Spring Boot app)
docker-compose up --build

# Start services in background
docker-compose up -d

# View logs
docker-compose logs -f app
docker-compose logs -f postgres

# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (clean database)
docker-compose down -v

# PostgreSQL connection (from host)
psql -h localhost -p 5432 -U admin -d employee_management
```

### Development Access Points
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Database Console** (local): http://localhost:8080/h2-console (sa/empty password)
- **Base API**: http://localhost:8080/api/employees | /api/departments

### PostgreSQL Access (Docker)
- **Host**: localhost:5432
- **Database**: employee_management  
- **Username**: admin
- **Password**: admin123

## Architecture & Code Structure

### Layered Architecture Pattern
- **Controllers**: REST endpoints with OpenAPI documentation, validation handling
- **Services**: Business logic layer with @Transactional management
- **Repositories**: JPA data access with custom query methods
- **Entities**: JPA entities with comprehensive Bean Validation constraints

### Database Design
- **Dual database support**: H2 (local dev) + PostgreSQL (Docker)
- **Profile-based configuration**: `application.properties` (H2), `application-docker.properties` (PostgreSQL)  
- **Bidirectional relationship**: Department (1) ↔ Employee (Many)
- **JPA configuration**: create-drop strategy, SQL logging enabled
- **Sample data**: Pre-loaded via DataLoader (works with both databases)

### Key Entity Relationships
```
Department ←→ Employee (bidirectional @OneToMany/@ManyToOne)
- Department.employees (List<Employee>, @JsonManagedReference)
- Employee.department (Department, @JsonBackReference)
```

### Validation Strategy
- **Field-level**: Jakarta validation annotations (@NotBlank, @Email, @Past, etc.)
- **Business rules**: Service layer validation (email uniqueness, salary increases)
- **Global error handling**: @RestControllerAdvice with structured responses
- **Exception mapping**: IllegalArgumentException→400, RuntimeException→404

### API Patterns
- **RESTful design**: Resource-based URLs, proper HTTP semantics
- **Advanced queries**: Search, filtering, date ranges, aggregations
- **Response patterns**: Consistent ResponseEntity usage
- **Documentation**: Complete OpenAPI 3.0 annotations with @Operation, @ApiResponse

## Key Business Features

### Employee Operations
- Full CRUD with employment status (ACTIVE/INACTIVE/TERMINATED)
- Advanced search: name, email, department, status, hire date, salary ranges
- Business operations: promote (salary validation), terminate (status update)
- Department assignment with relationship management

### Department Operations  
- CRUD with constraint validation (name uniqueness)
- Employee relationship management with cascade handling
- Statistics: employee count, average salary calculations
- Search and filtering capabilities

## Development Patterns

### Dependency Injection
- Constructor-based injection with @Autowired
- Final fields for immutable dependencies
- Clear dependency hierarchies

### Transaction Management
- @Transactional on service classes
- Method-level transactions for specific operations
- Proper transaction boundaries for business operations

### Error Handling
- Centralized @RestControllerAdvice
- Structured error responses with field-specific validation details
- Business rule enforcement with appropriate HTTP status codes

## Configuration Highlights
- **Auto-configuration**: Database, JPA, Web, Validation
- **Logging**: SQL logging enabled, debug for application package
- **CORS**: Enabled for frontend integration
- **H2 Console**: Enabled for database inspection

## Working with This Codebase

### Local Development (H2)
1. **Start development**: `mvn spring-boot:run` - sample data loads automatically
2. **API testing**: Use Swagger UI or H2 console to inspect data state

### Docker Development (PostgreSQL)  
1. **Start with Docker**: `docker-compose up --build` - full PostgreSQL setup
2. **View app logs**: `docker-compose logs -f app` 
3. **Connect to PostgreSQL**: Use provided connection details or any PostgreSQL client

### Common Development Tasks
1. **Adding endpoints**: Follow existing controller patterns with OpenAPI annotations
2. **Database changes**: Modify entities, both H2 and PostgreSQL will auto-update schema
3. **Business logic**: Add to service layer with proper transaction management  
4. **Validation**: Use existing patterns in entities and service methods
5. **Testing**: Sample data loads automatically in both environments