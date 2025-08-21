# Employee Management System

A comprehensive Spring Boot REST API application for managing employees and departments with dual database support (H2 for local development, PostgreSQL for Docker).

## 🚀 Features

- **Employee Management**: Full CRUD operations with employment status tracking
- **Department Management**: Department operations with employee relationships
- **Advanced Search**: Filter by name, department, salary range, hire date, status
- **Business Operations**: Employee promotion, termination with validation
- **API Documentation**: Complete OpenAPI/Swagger documentation
- **Dual Database Support**: H2 (local) + PostgreSQL (Docker)
- **Validation**: Comprehensive input validation and error handling
- **Docker Support**: Full containerization with PostgreSQL

## 🛠 Technology Stack

- **Spring Boot 3.2.0** with Java 21
- **Spring Data JPA** for database operations
- **H2 Database** (local development)
- **PostgreSQL** (Docker environment)
- **Spring Boot Validation** for input validation
- **Lombok** for reducing boilerplate code
- **SpringDoc OpenAPI** for API documentation
- **Spring Boot Actuator** for health checks
- **Maven** as build tool

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized setup)
- IntelliJ IDEA (for debugging)

## 🏃‍♂️ Quick Start

### Local Development (H2 Database)

```bash
# Clone the repository
git clone <repository-url>
cd EmployeeManagementApp

# Run the application
mvn spring-boot:run

# Access the application
# API: http://localhost:8080/api/employees
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console (sa/empty password)
```

### Docker Development (PostgreSQL)

```bash
# Build and start all services
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
```

## 🐳 Docker Setup

### Access Points
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **PostgreSQL**: localhost:5432 (admin/admin123)

### PostgreSQL Connection Details
- **Host**: localhost:5432
- **Database**: employee_management
- **Username**: admin
- **Password**: admin123

## 🐛 Debugging with IntelliJ (Docker Container)

You can debug your Spring Boot application running in Docker container using IntelliJ's remote debugging feature.

### 1. Enable Remote JVM Debug

The Dockerfile is already configured with debug support:
```dockerfile
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
```

The docker-compose.yml exposes the debug port:
```yaml
ports:
  - "8080:8080"
  - "5005:5005"  # Debug port
```

### 2. Start Docker Container
```bash
docker-compose up --build -d
```

Verify the debug port is listening:
```bash
docker-compose logs app | grep "Listening for transport"
# Should show: Listening for transport dt_socket at address: 5005
```

### 3. Configure IntelliJ Remote Debug

1. **Open Run/Debug Configurations**:
   - Go to `Run` → `Edit Configurations...`

2. **Add New Remote JVM Debug Configuration**:
   - Click `+` button → Select `Remote JVM Debug`

3. **Configure Settings**:
   - **Name**: `Docker Remote Debug`
   - **Debugger mode**: `Attach to remote JVM`
   - **Host**: `localhost`
   - **Port**: `5005`
   - **Use module classpath**: Select your project module
   - **Before launch**: Leave empty or add any pre-debug tasks

4. **Save Configuration**: Click `OK`

### 4. Start Debugging Session

1. **Set Breakpoints**: Place breakpoints in your code (e.g., `EmployeeController.java`)

2. **Start Debug Session**: 
   - Select "Docker Remote Debug" configuration
   - Click the debug icon (🐛) or press Shift+F9

3. **Verify Connection**: IntelliJ should connect to the remote JVM and show "Connected to the target VM"

### 5. Test Debugging

Make API calls to trigger your breakpoints:
```bash
# Test employee endpoint
curl -X GET "http://localhost:8080/api/employees/1"

# Test create employee
curl -X POST "http://localhost:8080/api/employees" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","phoneNumber":"+1234567890","hireDate":"2023-01-01","salary":50000,"employmentStatus":"ACTIVE"}'
```

### 6. Debug Features Available

- **Step Through Code**: Use F8 (step over), F7 (step into), Shift+F8 (step out)
- **Inspect Variables**: Hover over variables or use the Variables panel
- **Evaluate Expressions**: Alt+F8 to evaluate expressions
- **Watch Variables**: Add variables to the Watches panel
- **Hot Reload**: Limited hot swapping of method implementations

### 7. Debug Configuration Options

#### Suspend on Start (Optional)
To make the application wait for debugger connection, change the Dockerfile:
```dockerfile
# Wait for debugger connection before starting
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005", "-jar", "app.jar"]
```

#### Enhanced Debug Logging
Use the debug profile for more detailed logging:
```bash
# Set environment variable in docker-compose.yml
SPRING_PROFILES_ACTIVE: debug
```

### 8. Troubleshooting Debug Issues

**Connection Refused**:
- Ensure Docker container is running: `docker-compose ps`
- Check if debug port is exposed: `docker-compose logs app | grep "Listening"`
- Verify port 5005 is not blocked by firewall

**Breakpoints Not Hit**:
- Ensure you're using the same source code as the compiled version
- Check that the module classpath is correctly configured
- Verify the Java package names match

**Performance Issues**:
- Use `suspend=n` to avoid blocking the application startup
- Set specific breakpoints instead of broad exception breakpoints

## 📚 API Documentation

### Swagger UI
Access comprehensive API documentation at: http://localhost:8080/swagger-ui.html

### Key Endpoints

#### Employee Management
- `GET /api/employees` - List all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee
- `GET /api/employees/search?name={name}` - Search employees by name
- `PATCH /api/employees/{id}/promote?newSalary={salary}` - Promote employee

#### Department Management
- `GET /api/departments` - List all departments
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create new department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

### Sample API Calls

```bash
# Get all employees
curl http://localhost:8080/api/employees

# Create a new employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "hireDate": "2023-01-15",
    "salary": 75000,
    "employmentStatus": "ACTIVE"
  }'

# Search employees by name
curl "http://localhost:8080/api/employees/search?name=John"
```

## 🏗 Project Structure

```
src/main/java/com/coderahul/employeemanagement/
├── EmployeeManagementApplication.java    # Main Spring Boot application
├── config/
│   ├── DataLoader.java                   # Sample data initialization
│   └── OpenApiConfig.java               # Swagger/OpenAPI configuration
├── controller/
│   ├── EmployeeController.java          # Employee REST endpoints
│   └── DepartmentController.java        # Department REST endpoints
├── entity/
│   ├── Employee.java                    # Employee JPA entity
│   └── Department.java                  # Department JPA entity
├── exception/
│   └── GlobalExceptionHandler.java      # Centralized exception handling
├── repository/
│   ├── EmployeeRepository.java          # Employee data access layer
│   └── DepartmentRepository.java        # Department data access layer
└── service/
    ├── EmployeeService.java             # Employee business logic
    └── DepartmentService.java           # Department business logic
```

## 🔧 Configuration Profiles

### Local Development (default)
- Uses H2 in-memory database
- H2 console enabled at `/h2-console`
- Auto-loads sample data

### Docker Profile
- Uses PostgreSQL database
- Database configuration for containerized environment
- Health checks enabled

### Debug Profile
- Enhanced logging for debugging
- SQL query logging enabled
- Additional actuator endpoints exposed

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn -Dtest=EmployeeServiceTest test
```

## 📦 Building

```bash
# Clean and compile
mvn clean compile

# Build JAR file
mvn clean package

# Build without running tests
mvn clean package -DskipTests

# Build Docker image
docker-compose build
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Docker for containerization capabilities
- PostgreSQL for reliable database support
- OpenAPI for comprehensive API documentation