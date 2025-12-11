# Ubuntu Server Deployment Guide - INNOV8 Observability Lab

## 🚀 Complete Step-by-Step Guide to Deploy on Fresh Ubuntu Server

---

## Prerequisites
- Fresh Ubuntu Server (20.04 LTS or 22.04 LTS recommended)
- SSH access to the server
- sudo privileges
- Internet connection

---

## 📂 Project Location
Your project files are in: `/home/your-username/java-app`

---

## STEP 1: Update System Packages

```bash
# Connect to your Ubuntu server via SSH
ssh your-username@your-server-ip

# Update package list
sudo apt update

# Upgrade existing packages
sudo apt upgrade -y
```

---

## STEP 2: Install Java 21 (OpenJDK)

```bash
# Install OpenJDK 21
sudo apt install -y openjdk-21-jdk openjdk-21-jre

# Verify Java installation
java -version

# Expected output:
# openjdk version "21.0.x"
# OpenJDK Runtime Environment (build 21.0.x+xx)
# OpenJDK 64-Bit Server VM (build 21.0.x+xx, mixed mode, sharing)

# Set JAVA_HOME environment variable
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify JAVA_HOME
echo $JAVA_HOME
```

**Note**: If Java 21 is not available in your Ubuntu version, you can use:
```bash
# Alternative: Install from Adoptium/Eclipse Temurin
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo tee /etc/apt/keyrings/adoptium.asc
echo "deb [signed-by=/etc/apt/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
sudo apt update
sudo apt install -y temurin-21-jdk
```

---

## STEP 3: Install Maven

```bash
# Install Maven
sudo apt install -y maven

# Verify Maven installation
mvn -version

# Expected output:
# Apache Maven 3.x.x
# Maven home: /usr/share/maven
# Java version: 21.0.x

# If you need a specific Maven version, download directly:
cd /opt
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz
sudo tar -xzf apache-maven-3.9.11-bin.tar.gz
sudo ln -s /opt/apache-maven-3.9.11 /opt/maven

# Add Maven to PATH
echo 'export M2_HOME=/opt/maven' >> ~/.bashrc
echo 'export PATH=$M2_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

---

## STEP 4: Install Git (if needed to clone project)

```bash
# Install Git
sudo apt install -y git

# Verify
git --version
```

---

## STEP 5: Navigate to Your Project Directory

```bash
# Go to your project directory
cd ~/java-app

# Verify project files exist
ls -la

# You should see:
# pom.xml
# src/
# README.md
# etc.
```

---

## STEP 6: Build the Application

```bash
# Clean and compile the project
mvn clean compile

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX s
# [INFO] Finished at: YYYY-MM-DD...

# If build fails, check errors and ensure all files are present
```

---

## STEP 7: Package the Application

```bash
# Create JAR file
mvn clean package

# This creates: target/innov8-observability-lab-0.0.1-SNAPSHOT.jar

# Verify JAR was created
ls -lh target/*.jar
```

---

## STEP 8: Run the Application (Foreground Mode)

```bash
# Run the application
mvn spring-boot:run

# Or run the JAR directly:
java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar

# Expected output:
# ...
# Started Innov8ObservabilityLabApplication in X.XXX seconds
# Tomcat started on port(s): 8081 (http) with context path '/api'
```

**Testing**: Keep this terminal open and open a new SSH session for testing.

---

## STEP 9: Test the Application

Open a **new SSH terminal** or use `curl` from the same server:

```bash
# Test health endpoint
curl http://localhost:8081/api/actuator/health

# Expected: {"status":"UP"}

# Test personnel API
curl http://localhost:8081/api/personnel

# Expected: JSON array with 5 sample personnel records

# Test frontend (if accessing from browser)
# Open: http://your-server-ip:8081/index.html
```

---

## STEP 10: Configure Firewall (Allow Port 8081)

```bash
# Check if UFW firewall is active
sudo ufw status

# If inactive, enable it
sudo ufw enable

# Allow SSH (important - don't lock yourself out!)
sudo ufw allow ssh
sudo ufw allow 22/tcp

# Allow application port
sudo ufw allow 8081/tcp

# Verify rules
sudo ufw status numbered

# You should see:
# 8081/tcp                   ALLOW       Anywhere
```

---

## STEP 11: Access from External Browser

From your local computer:
```
http://your-server-ip:8081/index.html
```

**If you can't access**:
1. Check firewall: `sudo ufw status`
2. Check application is running: `ps aux | grep java`
3. Check port binding: `sudo netstat -tuln | grep 8081`
4. Check cloud provider security groups (AWS/Azure/GCP)

---

## STEP 12: Run Application as Background Service (Production Mode)

### Option A: Using `nohup` (Simple)

```bash
# Stop the foreground application (Ctrl+C if running)

# Run in background
nohup java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# Check it's running
ps aux | grep java

# View logs
tail -f app.log

# Get Process ID (PID)
ps aux | grep java

# To stop the application
kill -9 <PID>
```

### Option B: Using `systemd` Service (Recommended for Production)

Create a systemd service file:

```bash
# Create service file
sudo nano /etc/systemd/system/innov8-app.service
```

Add this content:
```ini
[Unit]
Description=INNOV8 Observability Lab Spring Boot Application
After=network.target

[Service]
Type=simple
User=your-username
WorkingDirectory=/home/your-username/java-app
ExecStart=/usr/bin/java -jar /home/your-username/java-app/target/innov8-observability-lab-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=append:/home/your-username/java-app/logs/app.log
StandardError=append:/home/your-username/java-app/logs/app-error.log

[Install]
WantedBy=multi-user.target
```

**Replace `your-username` with your actual username!**

```bash
# Create logs directory
mkdir -p ~/java-app/logs

# Reload systemd
sudo systemctl daemon-reload

# Enable service (start on boot)
sudo systemctl enable innov8-app

# Start service
sudo systemctl start innov8-app

# Check status
sudo systemctl status innov8-app

# View logs
sudo journalctl -u innov8-app -f

# Stop service
sudo systemctl stop innov8-app

# Restart service
sudo systemctl restart innov8-app
```

---

## STEP 13: Set Up Reverse Proxy with Nginx (Optional - Production)

If you want to use port 80/443 instead of 8081:

```bash
# Install Nginx
sudo apt install -y nginx

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/innov8-app
```

Add this content:
```nginx
server {
    listen 80;
    server_name your-domain.com;  # or your-server-ip

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/innov8-app /etc/nginx/sites-enabled/

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx

# Allow HTTP traffic
sudo ufw allow 'Nginx Full'

# Now access via: http://your-server-ip/index.html
```

---

## STEP 14: Set Up SSL Certificate with Let's Encrypt (Optional)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain certificate (replace with your domain)
sudo certbot --nginx -d your-domain.com

# Follow the prompts
# Certificate will auto-renew

# Test auto-renewal
sudo certbot renew --dry-run
```

---

## STEP 15: Monitor Application Logs

```bash
# Application logs (JSON format for Datadog)
tail -f ~/java-app/logs/innov8-app.json

# If running as systemd service
sudo journalctl -u innov8-app -f

# If running with nohup
tail -f ~/java-app/app.log

# Check for errors
grep -i error ~/java-app/logs/innov8-app.json
```

---

## STEP 16: Install Datadog Agent (For Monitoring)

```bash
# Add Datadog repository
DD_API_KEY=your-datadog-api-key bash -c "$(curl -L https://s3.amazonaws.com/dd-agent/scripts/install_script_agent7.sh)"

# Start Datadog agent
sudo systemctl start datadog-agent

# Check status
sudo datadog-agent status

# Configure log collection
sudo nano /etc/datadog-agent/conf.d/java.d/conf.yaml
```

Add:
```yaml
logs:
  - type: file
    path: /home/your-username/java-app/logs/innov8-app.json
    service: innov8-app
    source: java
    sourcecategory: sourcecode
    tags:
      - env:production
      - app:innov8-observability
```

```bash
# Restart Datadog agent
sudo systemctl restart datadog-agent
```

---

## 🔍 Verification Checklist

After completing all steps, verify:

- [ ] Java 21 installed: `java -version`
- [ ] Maven installed: `mvn -version`
- [ ] Project compiled: `mvn clean package` (BUILD SUCCESS)
- [ ] Application running: `ps aux | grep java`
- [ ] Port 8081 open: `sudo netstat -tuln | grep 8081`
- [ ] Firewall configured: `sudo ufw status`
- [ ] API responding: `curl http://localhost:8081/api/personnel`
- [ ] Health check: `curl http://localhost:8081/api/actuator/health`
- [ ] Frontend accessible: `http://your-server-ip:8081/index.html`
- [ ] Logs being created: `ls -la ~/java-app/logs/`
- [ ] Systemd service running (if configured): `sudo systemctl status innov8-app`

---

## 🛠️ Troubleshooting

### Issue: "Port 8081 already in use"
```bash
# Find process using port 8081
sudo lsof -i :8081

# Kill the process
kill -9 <PID>
```

### Issue: "Cannot connect from browser"
```bash
# Check if app is running
ps aux | grep java

# Check firewall
sudo ufw status

# Check cloud security groups (AWS/Azure/GCP)
# Allow inbound traffic on port 8081

# Try local access first
curl http://localhost:8081/api/actuator/health
```

### Issue: "BUILD FAILURE"
```bash
# Check Java version
java -version  # Must be 21+

# Check Maven version
mvn -version

# Clean Maven cache
rm -rf ~/.m2/repository
mvn clean package
```

### Issue: "Out of Memory"
```bash
# Run with more memory
java -Xmx1024m -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar

# Or in systemd service, modify ExecStart:
ExecStart=/usr/bin/java -Xmx1024m -jar /home/your-username/java-app/target/innov8-observability-lab-0.0.1-SNAPSHOT.jar
```

### Issue: "Application crashes"
```bash
# Check logs
tail -f ~/java-app/logs/innov8-app.json

# Check system resources
free -h
df -h
top
```

---

## 📊 Quick Commands Reference

```bash
# Build application
mvn clean package

# Run application (foreground)
java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar

# Run application (background)
nohup java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# Test API
curl http://localhost:8081/api/personnel

# Test health
curl http://localhost:8081/api/actuator/health

# View logs
tail -f logs/innov8-app.json

# Check running Java processes
ps aux | grep java

# Stop application (if running with nohup)
pkill -f innov8-observability-lab

# Service commands (if using systemd)
sudo systemctl start innov8-app
sudo systemctl stop innov8-app
sudo systemctl restart innov8-app
sudo systemctl status innov8-app
```

---

## 🌐 Access Points After Deployment

| Service | URL |
|---------|-----|
| Web Dashboard | `http://your-server-ip:8081/index.html` |
| API Base | `http://your-server-ip:8081/api` |
| Personnel API | `http://your-server-ip:8081/api/personnel` |
| Audit Logs | `http://your-server-ip:8081/api/audit-logs` |
| Health Check | `http://your-server-ip:8081/api/actuator/health` |
| Metrics | `http://your-server-ip:8081/api/actuator/metrics` |

---

## 🎯 Production Recommendations

1. **Use systemd service** instead of nohup
2. **Set up Nginx** as reverse proxy
3. **Enable SSL** with Let's Encrypt
4. **Configure log rotation**:
   ```bash
   sudo nano /etc/logrotate.d/innov8-app
   ```
   Add:
   ```
   /home/your-username/java-app/logs/*.log {
       daily
       rotate 7
       compress
       delaycompress
       missingok
       notifempty
       create 0640 your-username your-username
   }
   ```

5. **Set up monitoring** with Datadog
6. **Configure backups** for logs and data
7. **Set up proper database** (PostgreSQL/MySQL) instead of H2 for production

---

## 📝 Environment Variables (Production)

Create `.env` file or modify `application.yaml`:

```bash
# Database (for production)
spring.datasource.url=jdbc:postgresql://localhost:5432/innov8db
spring.datasource.username=innov8user
spring.datasource.password=secure_password

# Logging
logging.file.name=/var/log/innov8/app.log
logging.level.com.innov8=INFO

# Server
server.port=8081
server.servlet.context-path=/api
```

---

## ✅ Success Indicators

You'll know the deployment is successful when:
- ✅ Application starts without errors
- ✅ You can access the web dashboard from your browser
- ✅ API endpoints return data
- ✅ Logs are being written to `logs/innov8-app.json`
- ✅ Health check returns `{"status":"UP"}`
- ✅ You can create/delete personnel via the frontend
- ✅ Audit logs are being tracked

---

## 🚀 Next Steps

After successful deployment:
1. Test all API endpoints
2. Test frontend CRUD operations
3. Verify audit logging is working
4. Set up Datadog APM for monitoring
5. Configure production database
6. Set up automated backups
7. Configure SSL certificate
8. Set up monitoring alerts

---

**Deployment Date**: December 2025  
**Application**: INNOV8 Observability Lab  
**Support**: Check `APPLICATION_SUMMARY.md` for detailed documentation
