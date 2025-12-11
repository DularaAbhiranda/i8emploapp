# INNOV8 Datadog Observability Lab

A full-stack application demonstrating enterprise-grade observability, monitoring, and audit logging.

## 🎯 Quick Start

### Prerequisites
- Java 21+
- Node.js 20+
- npm 10+

### Run the Application

**Terminal 1 - Backend (Spring Boot)**
```bash
cd c:\Users\abhir\Desktop\Project
java -jar target/innov8-observability-lab-1.0.0.jar
# Or: mvn package -DskipTests && java -jar target/innov8-observability-lab-1.0.0.jar
```

**Terminal 2 - Frontend (React + Vite)**
```bash
cd c:\Users\abhir\Desktop\Project\frontend
npm run dev
```

**Access the Application**
- **Frontend**: http://localhost:5174
- **Backend API**: http://localhost:8081/api
- **Health Check**: http://localhost:8081/api/actuator/health

---

## 🏗️ Architecture & Features

### 1. **Advanced Background Processing** 🚀

#### Async Task Execution
- **SecurityAuditService**: Runs `runDeepScan()` asynchronously
  - Randomly sleeps 2-5 seconds (simulating CPU work)
  - Decorated with `@Async` for thread pool execution
  - Tagged with custom fields for APM visualization

#### Scheduled Jobs
- **HealthCheckJob**: Runs every 60 seconds
  - Queries inactive personnel count
  - Logs structured summaries
  - Triggers deep scans asynchronously
  - Perfect for APM timeline visualization

**Configuration:**
```yaml
# ThreadPool for async tasks (5 core, 10 max threads)
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=10
```

---

### 2. **Structured JSON Logging** 📊

#### Logback Configuration
JSON logs are written to `./logs/innov8-app.json` with the following fields:

```json
{
  "timestamp": "2025-12-11T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.innov8.service.PersonnelService",
  "message": "Retrieved 5 personnel records in 245ms",
  "correlationId": "abc-123-def",
  "userId": "user-001",
  "action": "GET /api/personnel",
  "executionTime": "245",
  "status": "SUCCESS",
  "thread": "innov8-async-1",
  "service": "innov8-observability-lab",
  "environment": "production"
}
```

#### MDC (Mapped Diagnostic Context)
- **RequestCorrelationInterceptor** attaches metadata to all requests:
  - `correlationId`: Unique request identifier
  - `userId`: User identifier (from headers)
  - `action`: HTTP method + URI
  
This enables full request tracing through distributed systems.

---

### 3. **Chaos & Traffic Generation** ⚡

#### Latency Jitter
In `PersonnelService.getAllPersonnel()`:
- **10% of requests** take an additional 1000ms
- Simulates real-world latency outliers
- Perfect for testing Datadog's latency detection

#### Intentional Error Endpoint
**Hidden endpoint for testing:**
```
GET /api/admin/simulate-error
```

- Throws `RuntimeException` with message: "Simulated Critical Failure"
- Logs at ERROR level with full context
- Ideal for testing:
  - Datadog Error Tracking
  - Alert rules
  - Incident response workflows

**WARNING:** This endpoint should be protected in production!

---

### 4. **Metrics Exposure** 📈

#### Spring Boot Actuator
All metrics endpoints are exposed:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

**Key endpoints for Datadog Agent scraping:**

| Endpoint | Purpose |
|----------|---------|
| `/actuator/metrics` | Lists all available metrics |
| `/actuator/metrics/jvm.memory.used` | Heap memory usage |
| `/actuator/metrics/jvm.gc.memory.promoted` | Garbage collection |
| `/actuator/metrics/process.uptime` | Process uptime |
| `/actuator/metrics/http.server.requests` | HTTP request metrics |
| `/actuator/health` | Custom health indicators |
| `/actuator/health/custom` | Custom health checks |

---

## Project Structure

```
src/main/java/com/innov8/
├── Innov8ObservabilityLabApplication.java
├── config/
│   ├── AsyncConfig.java              # Thread pool configuration
│   └── WebMvcConfig.java             # Interceptor registration
├── controller/
│   ├── PersonnelController.java      # CRUD endpoints
│   ├── AdminController.java          # Admin/test endpoints
│   └── CustomHealthIndicator.java    # Custom health checks
├── service/
│   ├── PersonnelService.java         # Business logic with logging
│   └── SecurityAuditService.java     # Async deep scan
├── job/
│   └── HealthCheckJob.java           # @Scheduled background job
├── model/
│   └── Personnel.java                # JPA Entity
├── repository/
│   └── PersonnelRepository.java      # Data access layer
├── interceptor/
│   └── RequestCorrelationInterceptor.java  # MDC setup
├── dto/
│   └── PersonnelDTO.java             # Data transfer object
├── exception/
│   └── GlobalExceptionHandler.java   # Centralized error handling
└── bootstrap/
    └── DataLoader.java               # Initial data loading

src/main/resources/
├── application.yaml                  # Spring configuration
└── logback-spring.xml               # JSON logging configuration
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Linux/macOS/Windows with terminal access

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd innov8-observability-lab
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Application starts on: `http://localhost:8081/api`

4. **Verify startup**
   ```bash
   curl http://localhost:8081/api/actuator/health
   ```

---

## Testing the Features

### 1. Test Personnel CRUD
```bash
# Get all personnel
curl http://localhost:8081/api/personnel

# Get single personnel
curl http://localhost:8081/api/personnel/1

# Create personnel
curl -X POST http://localhost:8081/api/personnel \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@innov8.lk",
    "role": "Tester",
    "department": "QA",
    "status": "ACTIVE"
  }'

# Update personnel
curl -X PUT http://localhost:8081/api/personnel/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated Name", ...}'

# Delete personnel
curl -X DELETE http://localhost:8081/api/personnel/1
```

### 2. Trigger Latency Jitter
```bash
# Run multiple times to see 10% with +1000ms latency
for i in {1..10}; do
  curl http://localhost:8081/api/personnel
done
```

### 3. Test Error Tracking
```bash
# Intentional error - triggers Datadog error tracking
curl http://localhost:8081/api/admin/simulate-error
```

### 4. Monitor Background Jobs
- HealthCheckJob runs every 60 seconds automatically
- Check logs: `tail -f ./logs/innov8-app.json`

### 5. View Metrics
```bash
# All available metrics
curl http://localhost:8081/api/actuator/metrics

# JVM Memory
curl http://localhost:8081/api/actuator/metrics/jvm.memory.used

# HTTP Requests
curl http://localhost:8081/api/actuator/metrics/http.server.requests
```

---

## Datadog Integration

### Installation Steps

1. **Install Datadog Agent on Ubuntu**
   ```bash
   DD_AGENT_MAJOR_VERSION=7 DD_API_KEY=<YOUR_API_KEY> \
   DD_SITE="datadoghq.com" bash -c "$(curl -L https://s3.amazonaws.com/dd-agent/scripts/install_script.sh)"
   ```

2. **Enable Java Tracing** (APM)
   ```bash
   # Download the Datadog Java Agent
   sudo mkdir -p /opt/datadog
   sudo wget -O /opt/datadog/dd-java-agent.jar https://dtdg.co/latest-java-tracer
   ```

3. **Configure Application Launch**
   ```bash
   java -javaagent:/opt/datadog/dd-java-agent.jar \
     -Ddd.service=innov8-observability-lab \
     -Ddd.env=production \
     -Ddd.version=1.0.0 \
     -Ddd.trace.sample.rate=1.0 \
     -Ddd.logs.injection=true \
     -jar innov8-observability-lab-1.0.0.jar
   ```

4. **Configure Log Collection**
   
   Add to `/etc/datadog-agent/conf.d/java.d/conf.yaml`:
   ```yaml
   logs:
     - type: file
       path: /path/to/app/logs/innov8-app.json
       service: innov8-observability-lab
       source: java
       parser: json
   ```

5. **Configure Metrics Scraping**
   
   Add to `/etc/datadog-agent/conf.d/spring_boot.d/conf.yaml`:
   ```yaml
   init_config:

   instances:
     - prometheus_url: http://localhost:8081/api/actuator/prometheus
       namespace: spring_boot
       metrics:
         - jvm.*
         - process.*
         - http.*
   ```

---

## Datadog Observability Features

### APM Traces 🎯
- View distributed traces for all requests
- See async task execution in separate spans
- Monitor HealthCheckJob execution timeline
- Track SecurityAuditService deep scans

### Logs 📝
- JSON-structured logs with correlation IDs
- Search by `correlationId` for full request journeys
- Filter by `userId`, `action`, `status`, `executionTime`
- View error stack traces with context

### Metrics 📊
- JVM heap memory usage and GC events
- HTTP request latency distribution
- Thread pool utilization
- Database connection pool metrics

### Error Tracking 🚨
- Automatic error detection from exception logs
- Custom `/api/admin/simulate-error` for alert testing
- Stack traces with source code context
- Affected users and services view

### Custom Monitors & Alerts
Example: Alert on high latency outliers
```
avg(last_5m):avg:trace.web.request.duration{service:innov8-observability-lab} > 2000
```

Example: Alert on error rate
```
avg(last_5m):avg:trace.web.request.errors{service:innov8-observability-lab} > 0.05
```

---

## Configuration Reference

### Application Properties (`application.yaml`)

```yaml
spring:
  application:
    name: innov8-observability-lab
  jpa:
    hibernate:
      ddl-auto: create-drop  # Auto-create schema
  datasource:
    url: jdbc:h2:mem:testdb  # In-memory H2 database
  h2:
    console:
      enabled: true  # Access H2 console at /api/h2-console

server:
  port: 8081
  servlet:
    context-path: /api

management:
  endpoints:
    web:
      exposure:
        include: "*"  # Expose all actuator endpoints
```

### Logback Configuration (`logback-spring.xml`)

**Appenders:**
- `FILE_JSON`: Writes JSON logs to `./logs/innov8-app.json`
- `ASYNC_FILE_JSON`: Async wrapper for performance
- `CONSOLE`: Human-readable console output

**Loggers:**
- `com.innov8`: DEBUG level
- `org.springframework`: WARN level
- `org.hibernate`: WARN level

---

## Performance Tuning

### Thread Pool Configuration
```java
// In AsyncConfig.java
executor.setCorePoolSize(5);      // Minimum threads
executor.setMaxPoolSize(10);      // Maximum threads
executor.setQueueCapacity(100);   // Task queue size
```

Adjust based on your load testing needs.

### Async Appender Tuning
```xml
<!-- In logback-spring.xml -->
<queueSize>512</queueSize>
<discardingThreshold>0</discardingThreshold>
```

- `queueSize`: Larger = more buffering, higher memory
- `discardingThreshold`: 0 = never drop logs

---

## Troubleshooting

### Logs not appearing in `./logs/innov8-app.json`
- Ensure `./logs` directory exists: `mkdir -p ./logs`
- Check permissions: `chmod 755 ./logs`
- Verify logback configuration is loaded

### Datadog Agent not receiving metrics
- Verify port 8081 is accessible
- Check agent configuration: `sudo systemctl status datadog-agent`
- Test connectivity: `curl http://localhost:8081/api/actuator/metrics`

### Async tasks not running
- Check thread pool: `curl http://localhost:8081/api/actuator/metrics/executor`
- Verify `@EnableAsync` is on main class
- Check logs for pool exhaustion errors

---

## Security Considerations

⚠️ **IMPORTANT:**
1. The `/api/admin/*` endpoints should be:
   - Protected by authentication in production
   - Behind VPN or internal network only
   - Disabled or removed from production builds

2. Actuator endpoints should be:
   - Protected with authentication
   - Rate-limited
   - Monitored for suspicious access

3. JSON logs contain:
   - User information (userIds)
   - Request details
   - Error stack traces
   - Ensure proper access controls on log files

---

## Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.3.5 | Framework |
| Java | 21 | Runtime |
| H2 Database | Latest | In-memory DB |
| Logstash Encoder | 8.0 | JSON logging |
| Datadog Java Agent | 1.43.0 | APM tracing |
| Lombok | Latest | Code generation |

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow existing code patterns
4. Ensure logs include MDC context
5. Submit a pull request

---

## Support

For issues or questions:
1. Check application logs: `./logs/innov8-app.json`
2. Review Datadog dashboards
3. Check actuator endpoints for metrics

---

## License

[Your License Here]

---

## Changelog

### v1.0.0 - Initial Release
- ✅ Async background processing
- ✅ Structured JSON logging with MDC
- ✅ Scheduled health check job
- ✅ Error simulation endpoint
- ✅ Latency jitter for realistic traffic
- ✅ Actuator metrics exposure
- ✅ Datadog integration ready
