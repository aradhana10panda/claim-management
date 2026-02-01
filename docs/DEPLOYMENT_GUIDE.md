# Deployment Guide - Claim Management Microservice

## 🚀 Deployment Overview

This guide covers various deployment strategies for the Claim Management Microservice, from local development to production environments.

## 📋 Deployment Options

1. **Local Development**: IDE or command line
2. **Standalone JAR**: Self-contained executable
3. **Docker Container**: Containerized deployment
4. **Cloud Platforms**: AWS, Azure, GCP
5. **Kubernetes**: Container orchestration

## 🏠 Local Development Deployment

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Running from IDE

1. **Import Project**: Import as Maven project
2. **Set JDK**: Configure Java 17
3. **Run Main Class**: Execute `ClaimManagementServiceApplication.main()`

### Running from Command Line

```bash
# Clone repository
git clone <repository-url>
cd claim-management-service

# Build project
mvn clean compile

# Run application
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/claim-management-service-1.0.0.jar
```

### Development Configuration

```yaml
# application-dev.yml
server:
  port: 8080

spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password: password
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    com.claimmanagement: DEBUG
```

## 📦 Standalone JAR Deployment

### Building Executable JAR

```bash
# Build with Maven
mvn clean package

# The JAR will be created at:
# target/claim-management-service-1.0.0.jar
```

### Running JAR

```bash
# Basic execution
java -jar claim-management-service-1.0.0.jar

# With custom profile
java -jar claim-management-service-1.0.0.jar --spring.profiles.active=prod

# With custom port
java -jar claim-management-service-1.0.0.jar --server.port=8081

# With JVM options
java -Xmx512m -Xms256m -jar claim-management-service-1.0.0.jar

# With external configuration
java -jar claim-management-service-1.0.0.jar --spring.config.location=classpath:/application.yml,/path/to/external/config.yml
```

### Production JAR Configuration

```yaml
# application-prod.yml
server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/claimdb
    username: ${DB_USERNAME:claimuser}
    password: ${DB_PASSWORD:claimpass}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  h2:
    console:
      enabled: false

logging:
  level:
    com.claimmanagement: INFO
    org.springframework: WARN
  file:
    name: /var/log/claim-management/application.log
```

## 🐳 Docker Deployment

### Dockerfile

```dockerfile
# Multi-stage build for optimized image
FROM openjdk:17-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build application
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jre-slim

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/claim-management-service-1.0.0.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Building Docker Image

```bash
# Build image
docker build -t claim-management-service:1.0.0 .

# Tag for registry
docker tag claim-management-service:1.0.0 your-registry/claim-management-service:1.0.0

# Push to registry
docker push your-registry/claim-management-service:1.0.0
```

### Running Docker Container

```bash
# Basic run
docker run -p 8080:8080 claim-management-service:1.0.0

# With environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=claimuser \
  -e DB_PASSWORD=claimpass \
  claim-management-service:1.0.0

# With volume for logs
docker run -p 8080:8080 \
  -v /host/logs:/var/log/claim-management \
  claim-management-service:1.0.0

# Detached mode
docker run -d -p 8080:8080 --name claim-service claim-management-service:1.0.0
```

### Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  claim-service:
    image: claim-management-service:1.0.0
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
      - DB_USERNAME=claimuser
      - DB_PASSWORD=claimpass
    depends_on:
      - postgres
    volumes:
      - ./logs:/var/log/claim-management
    restart: unless-stopped

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=claimdb
      - POSTGRES_USER=claimuser
      - POSTGRES_PASSWORD=claimpass
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: unless-stopped

volumes:
  postgres_data:
```

### Running with Docker Compose

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f claim-service

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build -d
```

## ☁️ Cloud Platform Deployment

### AWS Deployment

#### AWS Elastic Beanstalk

```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init claim-management-service

# Create environment
eb create production

# Deploy
eb deploy

# Open application
eb open
```

#### AWS ECS (Elastic Container Service)

```json
{
  "family": "claim-management-service",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "claim-service",
      "image": "your-account.dkr.ecr.region.amazonaws.com/claim-management-service:1.0.0",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/claim-management-service",
          "awslogs-region": "us-west-2",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Azure Deployment

#### Azure Container Instances

```bash
# Create resource group
az group create --name claim-management-rg --location eastus

# Create container instance
az container create \
  --resource-group claim-management-rg \
  --name claim-service \
  --image your-registry/claim-management-service:1.0.0 \
  --ports 8080 \
  --environment-variables SPRING_PROFILES_ACTIVE=prod \
  --cpu 1 \
  --memory 1
```

### Google Cloud Platform

#### Cloud Run

```bash
# Deploy to Cloud Run
gcloud run deploy claim-management-service \
  --image gcr.io/your-project/claim-management-service:1.0.0 \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080 \
  --memory 512Mi \
  --cpu 1
```

## ⚓ Kubernetes Deployment

### Kubernetes Manifests

#### Deployment

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: claim-management-service
  labels:
    app: claim-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: claim-service
  template:
    metadata:
      labels:
        app: claim-service
    spec:
      containers:
      - name: claim-service
        image: your-registry/claim-management-service:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: "postgres-service"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

#### Service

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: claim-management-service
spec:
  selector:
    app: claim-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### ConfigMap

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: claim-service-config
data:
  application.yml: |
    server:
      port: 8080
    spring:
      profiles:
        active: prod
    logging:
      level:
        com.claimmanagement: INFO
```

#### Secret

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: Y2xhaW11c2Vy  # base64 encoded 'claimuser'
  password: Y2xhaW1wYXNz  # base64 encoded 'claimpass'
```

### Deploying to Kubernetes

```bash
# Apply manifests
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml

# Check deployment status
kubectl get deployments
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/claim-management-service

# Scale deployment
kubectl scale deployment claim-management-service --replicas=5

# Update deployment
kubectl set image deployment/claim-management-service claim-service=your-registry/claim-management-service:1.1.0
```

### Helm Chart

```yaml
# Chart.yaml
apiVersion: v2
name: claim-management-service
description: A Helm chart for Claim Management Service
type: application
version: 1.0.0
appVersion: "1.0.0"
```

```yaml
# values.yaml
replicaCount: 3

image:
  repository: your-registry/claim-management-service
  tag: "1.0.0"
  pullPolicy: IfNotPresent

service:
  type: LoadBalancer
  port: 80
  targetPort: 8080

resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 250m
    memory: 256Mi

autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
```

## 🔧 Environment Configuration

### Environment Variables

```bash
# Database configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=claimdb
export DB_USERNAME=claimuser
export DB_PASSWORD=claimpass

# Application configuration
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=8080
export LOG_LEVEL=INFO

# Security configuration
export JWT_SECRET=your-jwt-secret
export ENCRYPTION_KEY=your-encryption-key
```

### Configuration Profiles

#### Development Profile

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:devdb
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true

logging:
  level:
    com.claimmanagement: DEBUG
```

#### Production Profile

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:claimdb}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    com.claimmanagement: INFO
  file:
    name: /var/log/claim-management/application.log
```

## 📊 Monitoring and Health Checks

### Health Check Endpoints

```yaml
# Actuator configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  health:
    db:
      enabled: true
```

### Custom Health Indicators

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Override
    public Health health() {
        try {
            long count = claimRepository.count();
            return Health.up()
                    .withDetail("database", "Available")
                    .withDetail("claimCount", count)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withException(e)
                    .build();
        }
    }
}
```

### Monitoring with Prometheus

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'claim-management-service'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

## 🚨 Troubleshooting

### Common Issues

#### Application Won't Start

```bash
# Check Java version
java -version

# Check port availability
netstat -an | grep 8080

# Check logs
tail -f /var/log/claim-management/application.log
```

#### Database Connection Issues

```bash
# Test database connectivity
telnet db-host 5432

# Check database credentials
psql -h db-host -U claimuser -d claimdb

# Verify connection string
echo $DB_URL
```

#### Memory Issues

```bash
# Check memory usage
docker stats container-name

# Increase heap size
java -Xmx1g -Xms512m -jar app.jar

# Monitor garbage collection
java -XX:+PrintGC -jar app.jar
```

### Debugging

```bash
# Enable debug mode
java -jar app.jar --debug

# Remote debugging
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar

# Profile application
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr -jar app.jar
```

## 📋 Deployment Checklist

### Pre-deployment

- [ ] Code reviewed and approved
- [ ] All tests passing
- [ ] Security scan completed
- [ ] Performance testing done
- [ ] Documentation updated

### Deployment

- [ ] Database migrations applied
- [ ] Configuration updated
- [ ] Secrets configured
- [ ] Health checks configured
- [ ] Monitoring setup

### Post-deployment

- [ ] Application health verified
- [ ] Smoke tests passed
- [ ] Monitoring alerts configured
- [ ] Rollback plan ready
- [ ] Team notified

## 🔄 CI/CD Pipeline

### GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build Docker image
        run: docker build -t claim-management-service:${{ github.sha }} .
      - name: Push to registry
        run: docker push your-registry/claim-management-service:${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/claim-management-service \
            claim-service=your-registry/claim-management-service:${{ github.sha }}
```

## 🎯 Best Practices

### Security

1. **Use Non-root User**: Run containers as non-root user
2. **Secrets Management**: Use proper secret management tools
3. **Network Security**: Implement proper network policies
4. **Image Scanning**: Scan container images for vulnerabilities
5. **HTTPS Only**: Use HTTPS in production

### Performance

1. **Resource Limits**: Set appropriate CPU and memory limits
2. **Health Checks**: Implement proper health checks
3. **Graceful Shutdown**: Handle shutdown signals properly
4. **Connection Pooling**: Configure database connection pooling
5. **Caching**: Implement appropriate caching strategies

### Reliability

1. **Multiple Replicas**: Run multiple instances
2. **Auto-scaling**: Configure horizontal pod autoscaling
3. **Circuit Breakers**: Implement circuit breaker patterns
4. **Retry Logic**: Add retry mechanisms for transient failures
5. **Monitoring**: Comprehensive monitoring and alerting

## 🎉 Conclusion

This deployment guide covers various deployment strategies from local development to production Kubernetes clusters. Choose the approach that best fits your infrastructure and requirements.

Key takeaways:
- Start simple with JAR deployment
- Use containers for consistency
- Implement proper monitoring
- Follow security best practices
- Plan for scalability

**Happy Deploying! 🚀**