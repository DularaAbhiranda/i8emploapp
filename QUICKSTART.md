# 🚀 Quick Start - Ubuntu Server Deployment

## Copy Project to Ubuntu Server

```bash
# On your local machine (from project directory)
# Replace 'username' and 'server-ip' with your values
scp -r c:\Users\abhir\Desktop\Project username@server-ip:~/java-app
```

Or use an alternative method:
- Upload via SFTP/FTP client (FileZilla, WinSCP)
- Clone from GitHub if your code is in a repository
- Use rsync for synchronization

---

## Automated Installation (RECOMMENDED)

```bash
# 1. SSH into your Ubuntu server
ssh username@your-server-ip

# 2. Navigate to project directory
cd ~/java-app

# 3. Make the script executable
chmod +x deploy-ubuntu.sh

# 4. Run the automated deployment script
./deploy-ubuntu.sh

# That's it! The script will:
# - Install Java 21
# - Install Maven
# - Build your application
# - Create systemd service
# - Start the application
# - Run health checks
```

---

## Manual Installation (Step by Step)

If you prefer manual control:

### 1. Update System
```bash
sudo apt update && sudo apt upgrade -y
```

### 2. Install Java 21
```bash
sudo apt install -y openjdk-21-jdk openjdk-21-jre
java -version  # Verify
```

### 3. Install Maven
```bash
sudo apt install -y maven
mvn -version  # Verify
```

### 4. Build Application
```bash
cd ~/java-app
mvn clean package
```

### 5. Run Application
```bash
# Option A: Foreground (for testing)
java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar

# Option B: Background (quick)
nohup java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# Option C: Systemd Service (production - see full guide)
```

### 6. Configure Firewall
```bash
sudo ufw allow 8081/tcp
sudo ufw status
```

### 7. Test Application
```bash
curl http://localhost:8081/api/actuator/health
curl http://localhost:8081/api/personnel
```

---

## Access Your Application

After deployment:

```
# From browser on any device:
http://YOUR-SERVER-IP:8081/index.html

# API Endpoints:
http://YOUR-SERVER-IP:8081/api/personnel
http://YOUR-SERVER-IP:8081/api/audit-logs
http://YOUR-SERVER-IP:8081/api/actuator/health
```

**Replace `YOUR-SERVER-IP` with your actual server IP address**

---

## Essential Commands

```bash
# Check if application is running
ps aux | grep java

# View application logs
tail -f ~/java-app/logs/app.log

# Stop application (if running with nohup)
pkill -f innov8-observability-lab

# If using systemd service:
sudo systemctl status innov8-app    # Check status
sudo systemctl start innov8-app     # Start
sudo systemctl stop innov8-app      # Stop
sudo systemctl restart innov8-app   # Restart
sudo journalctl -u innov8-app -f    # View logs
```

---

## Troubleshooting

### Can't access from browser?
1. Check firewall: `sudo ufw status`
2. Check service: `ps aux | grep java`
3. Check port: `sudo netstat -tuln | grep 8081`
4. Check cloud security group settings (AWS/Azure/GCP)

### Build failed?
```bash
# Check Java version (must be 21)
java -version

# Clean and rebuild
cd ~/java-app
mvn clean package
```

### Application won't start?
```bash
# Check logs
tail -f ~/java-app/logs/app.log

# Check if port is in use
sudo lsof -i :8081

# Try running in foreground to see errors
java -jar target/innov8-observability-lab-0.0.1-SNAPSHOT.jar
```

---

## Complete Documentation

For detailed step-by-step instructions, see:
- `UBUNTU_DEPLOYMENT_GUIDE.md` - Complete deployment guide
- `APPLICATION_SUMMARY.md` - Application features and API documentation

---

## Cloud Provider Notes

### AWS EC2
- Open port 8081 in Security Group
- Allow inbound TCP 8081 from 0.0.0.0/0

### Azure VM
- Open port 8081 in Network Security Group (NSG)
- Allow inbound TCP 8081

### Google Cloud (GCP)
- Open port 8081 in Firewall Rules
- Allow tcp:8081

---

## Production Recommendations

✅ Use systemd service (automated in script)  
✅ Set up Nginx reverse proxy (see full guide)  
✅ Configure SSL with Let's Encrypt (see full guide)  
✅ Set up log rotation  
✅ Install Datadog agent for monitoring  
✅ Use PostgreSQL/MySQL instead of H2 for production  

See `UBUNTU_DEPLOYMENT_GUIDE.md` for production setup details.

---

**Quick Help**: If you encounter any issues, check the logs first:
```bash
sudo journalctl -u innov8-app -f
```
