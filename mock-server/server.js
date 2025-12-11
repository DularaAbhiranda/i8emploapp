const express = require('express');
const cors = require('cors');
const app = express();
const port = 8081;

app.use(cors());
app.use(express.json());

// In-memory data store
let personnel = [
    { id: 1, name: "Amal Perera", email: "amal.perera@innov8.lk", role: "Security Analyst", department: "SOC", status: "ACTIVE" },
    { id: 2, name: "Nimal Fernando", email: "nimal.fernando@innov8.lk", role: "Penetration Tester", department: "Red Team", status: "ACTIVE" },
    { id: 3, name: "Kumari Silva", email: "kumari.silva@innov8.lk", role: "Incident Responder", department: "SOC", status: "ACTIVE" },
    { id: 4, name: "Rohan Jayawardena", email: "rohan.j@innov8.lk", role: "Security Engineer", department: "Engineering", status: "PENDING" },
    { id: 5, name: "Priya Wickramasinghe", email: "priya.w@innov8.lk", role: "Compliance Officer", department: "GRC", status: "INACTIVE" }
];

let auditLogs = [
    { timestamp: new Date().toISOString(), action: "CREATE", entityType: "PERSONNEL", username: "admin", ipAddress: "127.0.0.1", responseStatus: 200, executionTime: 45 },
    { timestamp: new Date(Date.now() - 50000).toISOString(), action: "READ", entityType: "PERSONNEL", username: "admin", ipAddress: "127.0.0.1", responseStatus: 200, executionTime: 120 }
];

// Routes
// Personnel
app.get('/api/personnel', (req, res) => {
    console.log(`[${new Date().toISOString()}] GET /api/personnel - 200`);
    // Simulate jitter
    if (Math.random() > 0.9) setTimeout(() => res.json(personnel), 1000);
    else res.json(personnel);
});

app.post('/api/personnel', (req, res) => {
    const newPerson = { id: personnel.length + 1, ...req.body, status: "ACTIVE" };
    personnel.push(newPerson);
    auditLogs.unshift({
        timestamp: new Date().toISOString(),
        action: "CREATE",
        entityType: "PERSONNEL",
        username: "admin",
        ipAddress: "::1",
        responseStatus: 201,
        executionTime: Math.floor(Math.random() * 50) + 10
    });
    console.log(`[${new Date().toISOString()}] POST /api/personnel - 201`);
    res.status(201).json(newPerson);
});

app.delete('/api/personnel/:id', (req, res) => {
    const id = parseInt(req.params.id);
    personnel = personnel.filter(p => p.id !== id);
    auditLogs.unshift({
        timestamp: new Date().toISOString(),
        action: "DELETE",
        entityType: "PERSONNEL",
        username: "admin",
        ipAddress: "::1",
        responseStatus: 200,
        executionTime: Math.floor(Math.random() * 50) + 10
    });
    console.log(`[${new Date().toISOString()}] DELETE /api/personnel/${id} - 200`);
    res.sendStatus(200);
});

// Audit Logs
app.get('/api/audit-logs', (req, res) => {
    console.log(`[${new Date().toISOString()}] GET /api/audit-logs - 200`);
    res.json(auditLogs);
});

// Admin Error Simulation
app.get('/api/admin/simulate-error', (req, res) => {
    console.log(`[${new Date().toISOString()}] GET /api/admin/simulate-error - 500`);
    res.status(500).json({ error: "Simulated Critical Failure", message: "This is a test error" });
});

// Health
app.get('/api/actuator/health', (req, res) => {
    res.json({ status: "UP" });
});

app.listen(port, () => {
    console.log(`🚀 Mock Backend running on http://localhost:${port}`);
    console.log(`   - Personnel API: http://localhost:${port}/api/personnel`);
    console.log(`   - Health Check:  http://localhost:${port}/api/actuator/health`);
});
