# INNOV8 Observability Lab - Project Structure

## 📁 Clean Project Organization

```
innov8-observability-lab/
├── frontend/                          # React + Vite Frontend
│   ├── src/
│   │   ├── App.jsx                   # Main React Component
│   │   ├── App.css                   # Component Styles
│   │   ├── main.jsx                  # Entry Point
│   │   └── index.css                 # Global Styles
│   ├── public/                        # Static Assets
│   ├── package.json                  # Frontend Dependencies
│   ├── vite.config.js                # Vite Config with API Proxy
│   └── index.html                    # HTML Template
│
├── src/                               # Backend (Java/Spring Boot)
│   ├── main/
│   │   ├── java/com/innov8/
│   │   │   ├── Innov8ObservabilityLabApplication.java
│   │   │   ├── bootstrap/
│   │   │   │   └── DataLoader.java        # Sample Data
│   │   │   ├── config/
│   │   │   │   ├── AsyncConfig.java       # Thread Pool
│   │   │   │   └── WebMvcConfig.java      # Web Config
│   │   │   ├── controller/
│   │   │   │   ├── PersonnelController.java
│   │   │   │   ├── AuditLogController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   └── CustomHealthIndicator.java
│   │   │   ├── dto/
│   │   │   │   ├── PersonnelDTO.java
│   │   │   │   └── AuditLogDTO.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── interceptor/
│   │   │   │   └── RequestCorrelationInterceptor.java
│   │   │   ├── job/
│   │   │   │   └── HealthCheckJob.java
│   │   │   ├── model/
│   │   │   │   ├── Personnel.java
│   │   │   │   ├── PersonnelStatus.java
│   │   │   │   └── AuditLog.java
│   │   │   ├── repository/
│   │   │   │   ├── PersonnelRepository.java
│   │   │   │   └── AuditLogRepository.java
│   │   │   └── service/
│   │   │       ├── PersonnelService.java
│   │   │       ├── SecurityAuditService.java
│   │   │       └── AuditLogService.java
│   │   └── resources/
│   │       ├── application.yaml        # Spring Boot Config
│   │       └── logback-spring.xml      # Logging Config
│   └── test/                           # (Optional) Test Classes
│
├── logs/                              # Application Logs
│   └── innov8-app.json               # JSON Structured Logs
│
├── target/                            # Maven Build Output
│   └── innov8-observability-lab-1.0.0.jar
│
├── pom.xml                            # Maven Dependencies
├── package.json                       # Frontend Dependencies (root)
├── .gitignore                         # Git Ignore Rules
├── README.md                          # Main Documentation
└── PROJECT_STRUCTURE.md               # This File
```

---

## 🎯 Component Overview

### Backend (Spring Boot 3.3.5)

**Location**: `/src/main/java/com/innov8/`

**Technologies**:
- Java 21
- Spring Boot 3.3.5
- Spring Framework 6.1.14
- Spring Data JPA
- H2 Database (In-Memory)
- Hibernate ORM
- Logback + Logstash (JSON Logging)
- Lombok (Boilerplate Reduction)

**API Endpoints**:
```
GET    /api/personnel                  # List all personnel
POST   /api/personnel                  # Create new personnel
GET    /api/personnel/{id}             # Get specific personnel
PUT    /api/personnel/{id}             # Update personnel
DELETE /api/personnel/{id}             # Delete personnel

GET    /api/audit-logs                 # Get audit logs
GET    /api/audit-logs/user/{name}     # User activity
GET    /api/audit-logs/action/{action} # Activity by action
GET    /api/actuator/health            # Health check
```

**Key Features**:
- ✅ RESTful CRUD API
- ✅ Audit logging (all actions tracked)
- ✅ Structured JSON logging
- ✅ Async task execution
- ✅ Scheduled health checks (every 60 seconds)
- ✅ Global exception handling
- ✅ MDC request correlation
- ✅ Datadog APM ready

---

### Frontend (React 19 + Vite)

**Location**: `/frontend/`

**Technologies**:
- React 19.2.0
- Vite 7.2.7
- Vanilla JavaScript (no Redux/Context)
- Fetch API for HTTP calls

**Features**:
- ✅ Real-time personnel management
- ✅ Live statistics dashboard
- ✅ Audit log viewer
- ✅ System health monitoring
- ✅ Responsive design
- ✅ Auto-refresh every 10 seconds
- ✅ API error handling

**API Integration**:
- Proxy configured in `vite.config.js`
- All calls to `/api/*` proxy to `http://localhost:8081`
- No hardcoded localhost references needed

---

## 🚀 Running the Application

### Development Mode

**Terminal 1 - Backend**
```bash
cd c:\Users\abhir\Desktop\Project
java -jar target/innov8-observability-lab-1.0.0.jar
# Runs on port 8081
```

**Terminal 2 - Frontend**
```bash
cd c:\Users\abhir\Desktop\Project\frontend
npm run dev
# Runs on port 5174
```

**Access**: http://localhost:5174

---

### Production Build

**Backend**:
```bash
mvn clean package
java -jar target/innov8-observability-lab-1.0.0.jar
```

**Frontend**:
```bash
npm run build
npm run preview
```

---

## 📊 Database Schema

### Personnel Table
```sql
CREATE TABLE personnel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'PENDING') NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Audit Logs Table
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    username VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    details TEXT,
    response_status INT,
    execution_time BIGINT,
    timestamp TIMESTAMP NOT NULL,
    user_agent TEXT
);
```

---

## 📝 Key Configuration Files

### Backend (`application.yaml`)
- Server port: 8081
- Context path: `/api`
- Database: H2 in-memory
- JPA/Hibernate settings
- Actuator endpoints enabled

### Frontend (`vite.config.js`)
- Development server proxy
- API routes to backend
- React Hot Module Replacement

### Logging (`logback-spring.xml`)
- Console output
- JSON file logging
- Async appenders
- Log levels per logger

---

## 🔄 Data Flow

```
User Browser (React)
    ↓ (http://localhost:5174)
    ↓ Makes API calls to /api/*
    ↓ (Vite proxy: localhost:5173 → localhost:8081)
    ↓
Spring Boot Backend (Java)
    ↓ (http://localhost:8081/api)
    ↓ Processes REST requests
    ↓ Logs to console & JSON file
    ↓ Stores data in H2 Database
    ↓
Response back to Frontend
    ↓ React updates UI
    ↓ Shows real-time data
```

---

## 🧹 Clean Structure

### What Was Removed
- ❌ Simple HTML dashboard (replaced with React)
- ❌ Mock server directory
- ❌ Deployment guide markdown files
- ❌ Unnecessary documentation

### Why Clean
- ✅ Single source of truth for frontend (React)
- ✅ Clear separation of concerns
- ✅ Reduced project bloat
- ✅ Easier to maintain
- ✅ Better for Git version control

---

## 📦 Dependencies

### Backend (pom.xml)
```xml
<!-- Core -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-actuator

<!-- Database -->
h2 (in-memory)

<!-- Utilities -->
lombok
jackson-core

<!-- Logging -->
logback-core
logstash-logback-encoder
```

### Frontend (package.json)
```json
{
  "react": "^19.2.0",
  "react-dom": "^19.2.0"
}

DevDependencies:
{
  "@vitejs/plugin-react": "^5.1.1",
  "vite": "^7.2.7",
  "eslint": "^9.39.1"
}
```

---

## 🔒 Security Features

- ✅ Global exception handling
- ✅ Request correlation IDs
- ✅ IP address logging
- ✅ User-Agent tracking
- ✅ Audit trail for all operations
- ✅ HTTP status code tracking
- ✅ Execution time metrics

---

## 📊 Monitoring & Observability

### Built-in Metrics
- ✅ Health endpoint: `/api/actuator/health`
- ✅ Structured JSON logs
- ✅ Execution time tracking
- ✅ Request/Response logging
- ✅ Error tracking

### Datadog Integration Ready
- ✅ JSON log format (Logstash encoder)
- ✅ MDC context for tracing
- ✅ Execution metrics
- ✅ Custom health indicators
- ✅ Request correlation

---

## ✅ Testing the Application

### Test Personnel Creation
```bash
curl -X POST http://localhost:8081/api/personnel \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@test.com","role":"Admin","department":"IT"}'
```

### Test Data Retrieval
```bash
curl http://localhost:8081/api/personnel
```

### Check Health
```bash
curl http://localhost:8081/api/actuator/health
```

---

## 🎯 Next Steps for Production

1. **Replace H2 with PostgreSQL/MySQL**
   - Update pom.xml with PostgreSQL dependency
   - Update application.yaml with production DB settings

2. **Set up Datadog Agent**
   - Download Datadog APM agent
   - Configure log ingestion

3. **Enable HTTPS**
   - Configure SSL certificates
   - Update CORS settings

4. **Set up Reverse Proxy**
   - Use Nginx or Apache
   - Route frontend and backend requests

5. **Container Deployment**
   - Create Dockerfile for backend
   - Build frontend static files
   - Deploy to Docker/Kubernetes

6. **Database Persistence**
   - Migrate from H2 to production database
   - Set up automated backups

---

## 📄 License & Credits

**INNOV8 Observability Lab**
- Created: December 2025
- Production-grade React + Spring Boot full-stack
- Designed for Datadog APM integration
- Clean, maintainable architecture

---

**Status**: ✅ Ready for Development & Production Deployment
