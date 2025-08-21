# 🐛 Docker Remote Debugging Guide for IntelliJ IDEA

This guide walks you through setting up remote debugging for a Spring Boot application running in a Docker container using IntelliJ IDEA.

## 📋 Prerequisites

- IntelliJ IDEA (Community or Ultimate)
- Docker and Docker Compose installed
- Spring Boot application with Docker setup
- Java 21 (or your project's Java version)

## 🔧 Step 1: Docker Configuration for Debugging

### 1.1 Configure Dockerfile for Debug Support

Your `Dockerfile` should include JVM debug parameters:

```dockerfile
# Runtime stage with minimal JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install wget for health checks and create user
RUN apk add --no-cache wget && \
    addgroup -g 1001 -S appgroup && \
    adduser -S -D -H -u 1001 -s /sbin/nologin -G appgroup appuser

# Copy the JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

USER appuser

# Expose application and debug ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application with debug support
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
```

**Key Debug Parameters Explained:**
- `-agentlib:jdwp`: Enables Java Debug Wire Protocol
- `transport=dt_socket`: Uses socket transport
- `server=y`: JVM acts as debug server
- `suspend=n`: Don't wait for debugger connection to start
- `address=*:5005`: Listen on all interfaces, port 5005

### 1.2 Update docker-compose.yml

Ensure your `docker-compose.yml` exposes the debug port:

```yaml
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: employee-management-app
    ports:
      - "8080:8080"    # Application port
      - "5005:5005"    # Debug port
    environment:
      SPRING_PROFILES_ACTIVE: docker
      # ... other environment variables
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - employee-network
    restart: unless-stopped
```

## 🚀 Step 2: Start Docker Container with Debug Support

### 2.1 Build and Start Container

```bash
# Stop existing containers
docker-compose down

# Build and start with debug support
docker-compose up --build -d

# Verify containers are running
docker-compose ps
```

### 2.2 Verify Debug Port is Active

```bash
# Check debug port is listening
docker-compose logs app | grep "Listening for transport"

# Expected output:
# Listening for transport dt_socket at address: 5005
```

### 2.3 Verify Application is Healthy

```bash
# Test application health
curl http://localhost:8080/actuator/health

# Expected output:
# {"status":"UP"}
```

## 🔧 Step 3: Configure IntelliJ Remote Debug

### 3.1 Open Run/Debug Configurations

**Method 1 - Via Menu:**
1. Go to `Run` → `Edit Configurations...`

**Method 2 - Via Toolbar:**
1. Click the dropdown next to the Run button (usually shows your main class)
2. Select `Edit Configurations...`

### 3.2 Create Remote Debug Configuration

1. **Add New Configuration:**
   - Click the `+` (plus) icon in the top-left corner
   - Select `Remote JVM Debug` from the dropdown

2. **Configure Debug Settings:**
   ```
   Name: Docker Remote Debug
   Debugger mode: Attach to remote JVM
   Transport: Socket
   Host: localhost
   Port: 5005
   Use module classpath: EmployeeManagementApp (select your project module)
   Before launch: (leave empty or add any pre-debug tasks)
   ```

3. **Save Configuration:**
   - Click `Apply`
   - Click `OK`

### 3.3 Visual Configuration Guide

```
┌─ Run/Debug Configurations ──────────────────────────────────┐
│ [+] [-] [^] [v]  Templates                                  │
│ ├─ Application                                              │
│ ├─ Remote JVM Debug                                         │
│ │  └─ Docker Remote Debug  ✓                               │
│ └─ ...                                                      │
│                                                             │
│ Configuration tab:                                          │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ Name: [Docker Remote Debug                        ]     │ │
│ │ Debugger mode: [Attach to remote JVM            ▼]     │ │
│ │ Transport: [Socket                               ▼]     │ │
│ │ Host: [localhost                                 ]      │ │
│ │ Port: [5005                                     ]      │ │
│ │ Use module classpath: [EmployeeManagementApp     ▼]     │ │
│ └─────────────────────────────────────────────────────────┘ │
│                                                             │
│ [Apply] [Cancel] [OK]                                       │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Step 4: Set Breakpoints

### 4.1 Open Your Controller File

Navigate to `src/main/java/com/coderahul/employeemanagement/controller/EmployeeController.java`

### 4.2 Set Breakpoints

1. **Click in the left gutter** (next to line numbers) to set breakpoints
2. **Good locations for breakpoints:**
   ```java
   @GetMapping
   public ResponseEntity<List<Employee>> getAllEmployees() {
       List<Employee> employees = employeeService.getAllEmployees(); // ← Set breakpoint here
       return ResponseEntity.ok(employees);
   }
   
   @GetMapping("/{id}")
   public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
       return employeeService.getEmployeeById(id) // ← Set breakpoint here
               .map(employee -> ResponseEntity.ok(employee))
               .orElse(ResponseEntity.notFound().build());
   }
   ```

3. **Breakpoint Indicators:**
   - Red dot = Active breakpoint
   - Gray dot = Disabled breakpoint
   - Checkmark = Verified breakpoint (when debugging)

### 4.3 Breakpoint Management

**Right-click on breakpoint for options:**
- Enable/Disable breakpoint
- Edit breakpoint conditions
- Add logging without stopping execution

## 🚀 Step 5: Start Debug Session

### 5.1 Select Debug Configuration

1. **Find the run configuration dropdown** (top toolbar)
2. **Select "Docker Remote Debug"** from the dropdown

### 5.2 Start Debugging

**Method 1 - Click Debug Button:**
- Click the 🐛 (bug/beetle) icon next to the run dropdown

**Method 2 - Keyboard Shortcut:**
- Press `Shift + F9`

**Method 3 - Menu:**
- Go to `Run` → `Debug 'Docker Remote Debug'`

### 5.3 Verify Debug Connection

You should see in the Debug console:
```
Connected to the target VM, address: 'localhost:5005', transport: 'socket'
```

## 🧪 Step 6: Test Your Debug Setup

### 6.1 Make API Calls to Trigger Breakpoints

**Test Employee Endpoints:**
```bash
# Get all employees (should hit getAllEmployees breakpoint)
curl -X GET "http://localhost:8080/api/employees"

# Get specific employee (should hit getEmployeeById breakpoint)
curl -X GET "http://localhost:8080/api/employees/1"

# Create new employee (should hit createEmployee breakpoint)
curl -X POST "http://localhost:8080/api/employees" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Debug",
    "lastName": "Test",
    "email": "debug.test@example.com",
    "phoneNumber": "+1234567890",
    "hireDate": "2023-01-01",
    "salary": 50000,
    "employmentStatus": "ACTIVE"
  }'
```

### 6.2 Expected Debug Behavior

When a breakpoint is hit:
1. **Execution pauses** at the breakpoint line
2. **IntelliJ comes to foreground** with debug perspective
3. **Variables panel** shows current variable values
4. **Call stack** shows the execution path

## 🎮 Step 7: Debug Controls and Features

### 7.1 Debug Toolbar Controls

```
[▶ Resume] [⏸ Pause] [⏹ Stop] [🔄 Restart] [F8 Step Over] [F7 Step Into] [Shift+F8 Step Out]
```

**Key Shortcuts:**
- `F9` - Resume program execution
- `F8` - Step over (execute current line, don't go into method calls)
- `F7` - Step into (enter method calls)
- `Shift + F8` - Step out (exit current method)
- `Alt + F8` - Evaluate expression
- `Ctrl + F8` - Toggle breakpoint

### 7.2 Debug Windows

**Variables Panel:**
- Shows all variables in current scope
- Expand objects to see their properties
- Right-click to set variable values

**Watches Panel:**
- Add expressions to monitor continuously
- Useful for complex object properties

**Call Stack:**
- Shows the sequence of method calls
- Click different stack frames to inspect

**Console:**
- Shows debug connection status
- Application logs and debug output

### 7.3 Advanced Debug Features

**Conditional Breakpoints:**
1. Right-click on breakpoint
2. Add condition (e.g., `id == 1`)
3. Breakpoint only triggers when condition is true

**Evaluate Expressions:**
1. Highlight code or variable
2. Press `Alt + F8`
3. Evaluate any Java expression in current context

**Set Variable Values:**
1. In Variables panel, right-click variable
2. Select "Set Value"
3. Enter new value for testing

## 🔧 Step 8: Debug Configuration Variations

### 8.1 Suspend on Start (Wait for Debugger)

If you want the application to wait for debugger connection:

```dockerfile
# Change suspend=n to suspend=y
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005", "-jar", "app.jar"]
```

**Use Case:** Debug application startup issues

### 8.2 Enhanced Debug Logging

Create `application-debug.properties`:
```properties
# Enhanced Debug Logging
logging.level.com.coderahul.employeemanagement=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Show SQL parameters
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Use debug profile:
```yaml
# In docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: debug
```

### 8.3 Debug Multiple Services

For debugging multiple containers:
```yaml
services:
  app1:
    ports:
      - "5005:5005"  # Debug port for app1
  app2:
    ports:
      - "5006:5005"  # Debug port for app2 (different host port)
```

## 🔍 Step 9: Troubleshooting Debug Issues

### 9.1 Connection Issues

**Problem:** "Connection refused" or "Unable to connect"

**Solutions:**
```bash
# 1. Check if container is running
docker-compose ps

# 2. Check if debug port is listening
docker-compose logs app | grep "Listening for transport"

# 3. Check port mapping
docker-compose port app 5005

# 4. Test port connectivity
telnet localhost 5005
# or
nc -zv localhost 5005
```

### 9.2 Breakpoints Not Working

**Problem:** Breakpoints are ignored or gray

**Solutions:**
1. **Verify source code matches compiled version**
   - Rebuild and restart containers: `docker-compose up --build`

2. **Check module classpath in debug configuration**
   - Ensure correct module is selected

3. **Verify breakpoint is in executed code path**
   - Add logging to confirm method is called

4. **Check if classes are loaded**
   - Use "Loaded Classes" in debugger to verify

### 9.3 Performance Issues

**Problem:** Application runs slowly during debugging

**Solutions:**
1. **Use specific breakpoints instead of exception breakpoints**
2. **Disable unnecessary breakpoints**
3. **Use logging breakpoints for monitoring without stopping**
4. **Consider using `suspend=y` only when needed**

### 9.4 Port Conflicts

**Problem:** Port 5005 already in use

**Solutions:**
```yaml
# Change debug port in docker-compose.yml
ports:
  - "5006:5005"  # Use different host port

# Update IntelliJ debug configuration
Port: 5006
```

### 9.5 Container Restart Issues

**Problem:** Debug connection lost after container restart

**Solutions:**
1. **Restart debug session in IntelliJ**
2. **Check container health:**
   ```bash
   docker-compose logs app
   curl http://localhost:8080/actuator/health
   ```

## 📋 Step 10: Debug Session Checklist

### Pre-Debug Checklist:
- [ ] Docker containers are running (`docker-compose ps`)
- [ ] Debug port is listening (check logs)
- [ ] Application is healthy (`curl health endpoint`)
- [ ] Breakpoints are set in relevant code
- [ ] IntelliJ debug configuration is correct

### During Debug Session:
- [ ] Debug connection established
- [ ] Breakpoints are verified (red dots with checkmarks)
- [ ] Variables panel shows current values
- [ ] Step controls work properly

### Post-Debug Actions:
- [ ] Stop debug session in IntelliJ
- [ ] Optionally stop Docker containers
- [ ] Remove temporary breakpoints
- [ ] Document any issues found

## 🚀 Quick Reference Commands

### Docker Commands:
```bash
# Start debug environment
docker-compose up --build -d

# Check debug status
docker-compose logs app | grep "Listening for transport"

# Stop environment
docker-compose down

# Clean restart
docker-compose down -v && docker-compose up --build -d
```

### IntelliJ Shortcuts:
```
Shift + F9    - Start debugging
F9            - Resume execution
F8            - Step over
F7            - Step into
Shift + F8    - Step out
Alt + F8      - Evaluate expression
Ctrl + F8     - Toggle breakpoint
Ctrl + Shift + F8 - View all breakpoints
```

### Test Commands:
```bash
# Basic endpoint test
curl http://localhost:8080/api/employees

# Health check
curl http://localhost:8080/actuator/health

# Test with specific employee
curl http://localhost:8080/api/employees/1
```

## 🎯 Common Use Cases

### 1. Debug API Request Flow
- Set breakpoints in controller methods
- Step through service layer logic
- Inspect request/response objects

### 2. Debug Database Operations
- Set breakpoints in service methods
- Watch SQL queries in logs
- Inspect entity objects before/after persistence

### 3. Debug Validation Issues
- Set breakpoints in validation logic
- Check field values and validation results
- Trace exception handling flow

### 4. Debug Business Logic
- Step through complex calculations
- Watch variable changes over time
- Evaluate expressions for testing scenarios

## 📚 Additional Resources

- [IntelliJ IDEA Debugging Guide](https://www.jetbrains.com/help/idea/debugging-code.html)
- [Java Debug Wire Protocol (JDWP)](https://docs.oracle.com/en/java/javase/11/docs/specs/jpda/jdwp-spec.html)
- [Spring Boot Debugging](https://spring.io/guides/gs/intellij-idea/)
- [Docker Debug Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Happy Debugging! 🐛🚀**

Remember: Debugging is a powerful tool for understanding your application's behavior. Use it to step through code, inspect variables, and trace execution flow to quickly identify and fix issues.