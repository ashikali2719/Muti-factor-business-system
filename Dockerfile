# Multi-stage build for Java backend
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven config and source
COPY pom.xml .
COPY src ./src
COPY data ./data

# Build WAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime - Minimal image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install Jetty runtime
RUN apk add --no-cache curl

# Copy WAR from builder
COPY --from=builder /app/target/*.war /app/

# Copy data directory
COPY --from=builder /app/data ./data

# Expose port 8090
EXPOSE 8090

# Start Jetty with optimized settings for Container Apps
CMD ["java", \
     "-Xmx256m", \
     "-Xms128m", \
     "-Djetty.port=8090", \
     "-cp", "/app/*", \
     "-jar", "/opt/jetty/start.jar"]

# Alternative: Use Jetty runner (simpler)
# Download jetty-runner and run with:
# java -Xmx256m -jar jetty-runner.jar --port 8090 business-decision-system-1.0-SNAPSHOT.war
