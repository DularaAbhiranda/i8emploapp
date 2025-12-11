#!/bin/bash
#############################################################
# INNOV8 Observability Lab - Ubuntu Deployment Script
# Automated Installation Script for Fresh Ubuntu Server
#############################################################

set -e  # Exit on any error

echo "=========================================="
echo "INNOV8 Observability Lab - Automated Setup"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}➜ $1${NC}"
}

#############################################################
# STEP 1: Update System
#############################################################
print_info "Step 1: Updating system packages..."
sudo apt update -y
sudo apt upgrade -y
print_success "System packages updated"
echo ""

#############################################################
# STEP 2: Install Java 21
#############################################################
print_info "Step 2: Installing Java 21..."

# Check if Java 21 is already installed
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$JAVA_VERSION" == "21."* ]]; then
        print_success "Java 21 is already installed"
    else
        print_info "Installing OpenJDK 21..."
        sudo apt install -y openjdk-21-jdk openjdk-21-jre
    fi
else
    print_info "Installing OpenJDK 21..."
    sudo apt install -y openjdk-21-jdk openjdk-21-jre
fi

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Add to bashrc if not already there
if ! grep -q "JAVA_HOME" ~/.bashrc; then
    echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
print_success "Java installed: $JAVA_VERSION"
echo ""

#############################################################
# STEP 3: Install Maven
#############################################################
print_info "Step 3: Installing Maven..."

if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    print_success "Maven is already installed: $MVN_VERSION"
else
    sudo apt install -y maven
    MVN_VERSION=$(mvn -version | head -n 1)
    print_success "Maven installed: $MVN_VERSION"
fi
echo ""

#############################################################
# STEP 4: Navigate to Project Directory
#############################################################
print_info "Step 4: Checking project directory..."

PROJECT_DIR="$HOME/java-app"

if [ ! -d "$PROJECT_DIR" ]; then
    print_error "Project directory not found: $PROJECT_DIR"
    print_info "Please create it with: mkdir -p $PROJECT_DIR"
    print_info "And copy your project files there"
    exit 1
fi

cd "$PROJECT_DIR"
print_success "Found project directory: $PROJECT_DIR"

if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found in $PROJECT_DIR"
    print_info "Please ensure your project files are in this directory"
    exit 1
fi
print_success "Found pom.xml"
echo ""

#############################################################
# STEP 5: Build the Application
#############################################################
print_info "Step 5: Building the application..."
print_info "Running: mvn clean package"

mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    print_success "Application built successfully!"
else
    print_error "Build failed. Please check the errors above."
    exit 1
fi
echo ""

#############################################################
# STEP 6: Create Logs Directory
#############################################################
print_info "Step 6: Creating logs directory..."
mkdir -p "$PROJECT_DIR/logs"
print_success "Logs directory created: $PROJECT_DIR/logs"
echo ""

#############################################################
# STEP 7: Configure Firewall
#############################################################
print_info "Step 7: Configuring firewall..."

if command -v ufw &> /dev/null; then
    # Check if UFW is installed
    UFW_STATUS=$(sudo ufw status | grep -o "Status: active" || echo "inactive")
    
    if [[ "$UFW_STATUS" == "Status: active" ]]; then
        print_info "UFW is active, adding firewall rules..."
        sudo ufw allow 8081/tcp
        print_success "Port 8081 allowed in firewall"
    else
        print_info "UFW is not active. To enable it:"
        print_info "  sudo ufw enable"
        print_info "  sudo ufw allow ssh"
        print_info "  sudo ufw allow 8081/tcp"
    fi
else
    print_info "UFW not installed. Skipping firewall configuration."
fi
echo ""

#############################################################
# STEP 8: Create systemd Service
#############################################################
print_info "Step 8: Creating systemd service..."

SERVICE_FILE="/etc/systemd/system/innov8-app.service"
JAR_FILE="$PROJECT_DIR/target/innov8-observability-lab-0.0.1-SNAPSHOT.jar"
CURRENT_USER=$(whoami)

sudo tee "$SERVICE_FILE" > /dev/null <<EOF
[Unit]
Description=INNOV8 Observability Lab Spring Boot Application
After=network.target

[Service]
Type=simple
User=$CURRENT_USER
WorkingDirectory=$PROJECT_DIR
ExecStart=/usr/bin/java -jar $JAR_FILE
Restart=on-failure
RestartSec=10
StandardOutput=append:$PROJECT_DIR/logs/app.log
StandardError=append:$PROJECT_DIR/logs/app-error.log

[Install]
WantedBy=multi-user.target
EOF

print_success "Systemd service file created: $SERVICE_FILE"
echo ""

#############################################################
# STEP 9: Enable and Start Service
#############################################################
print_info "Step 9: Starting the application service..."

sudo systemctl daemon-reload
sudo systemctl enable innov8-app
sudo systemctl start innov8-app

# Wait for service to start
sleep 5

# Check service status
if sudo systemctl is-active --quiet innov8-app; then
    print_success "Application service started successfully!"
else
    print_error "Service failed to start. Checking status..."
    sudo systemctl status innov8-app
    exit 1
fi
echo ""

#############################################################
# STEP 10: Test the Application
#############################################################
print_info "Step 10: Testing the application..."

# Wait a bit more for Spring Boot to fully start
print_info "Waiting for Spring Boot to fully initialize (15 seconds)..."
sleep 15

# Test health endpoint
print_info "Testing health endpoint..."
HEALTH_RESPONSE=$(curl -s http://localhost:8081/api/actuator/health)

if [[ "$HEALTH_RESPONSE" == *"UP"* ]]; then
    print_success "Health check passed: $HEALTH_RESPONSE"
else
    print_error "Health check failed. Response: $HEALTH_RESPONSE"
fi

# Test personnel API
print_info "Testing personnel API..."
PERSONNEL_RESPONSE=$(curl -s http://localhost:8081/api/personnel | head -c 100)

if [[ "$PERSONNEL_RESPONSE" == *"Amal"* ]] || [[ "$PERSONNEL_RESPONSE" == "["* ]]; then
    print_success "Personnel API is responding"
else
    print_error "Personnel API test failed. Response: $PERSONNEL_RESPONSE"
fi
echo ""

#############################################################
# STEP 11: Display Summary
#############################################################
echo "=========================================="
echo "DEPLOYMENT COMPLETE!"
echo "=========================================="
echo ""
print_success "Application is running!"
echo ""
echo "Service Status:"
sudo systemctl status innov8-app --no-pager | head -n 10
echo ""
echo "Access Points:"
echo "  - Web Dashboard: http://$(hostname -I | awk '{print $1}'):8081/index.html"
echo "  - API Base: http://$(hostname -I | awk '{print $1}'):8081/api"
echo "  - Health Check: http://$(hostname -I | awk '{print $1}'):8081/api/actuator/health"
echo ""
echo "Useful Commands:"
echo "  - View logs: sudo journalctl -u innov8-app -f"
echo "  - Stop service: sudo systemctl stop innov8-app"
echo "  - Start service: sudo systemctl start innov8-app"
echo "  - Restart service: sudo systemctl restart innov8-app"
echo "  - Check status: sudo systemctl status innov8-app"
echo ""
echo "Log Files:"
echo "  - Application logs: $PROJECT_DIR/logs/app.log"
echo "  - Error logs: $PROJECT_DIR/logs/app-error.log"
echo "  - JSON logs (Datadog): $PROJECT_DIR/logs/innov8-app.json"
echo ""
print_info "Note: If accessing from external network, ensure port 8081 is allowed"
print_info "in your cloud provider's security group (AWS/Azure/GCP)."
echo ""
print_success "Setup completed successfully! 🚀"
echo "=========================================="
