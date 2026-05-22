# ============================================
# CyberScribeX - Production Dockerfile
# Multi-stage build for optimized image size
# ============================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Sajjad Salaria <xoraus@protonmail.com>"
LABEL description="CyberScribeX - Production-grade Blogging Platform REST API"

# Security: Run as non-root user
RUN addgroup -S cyberscribex && adduser -S cyberscribex -G cyberscribex

WORKDIR /app

# Copy built artifact
COPY --from=builder /app/target/CyberScribeX-1.0.0.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R cyberscribex:cyberscribex /app

USER cyberscribex

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
