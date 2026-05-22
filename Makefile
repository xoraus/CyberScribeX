# ============================================
# CyberScribeX - Makefile
# ============================================

.PHONY: help build run test clean docker-build docker-run docker-stop migrate dev prod

# Default target
help: ## Show this help message
	@echo "CyberScribeX - Available Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project
	./mvnw clean compile

test: ## Run all tests
	./mvnw clean test

test-integration: ## Run integration tests only
	./mvnw clean verify -P integration-test

package: ## Package the application
	./mvnw clean package -DskipTests

dev: ## Run in development mode
	SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

prod: ## Run in production mode (requires env vars)
	SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run

docker-build: ## Build Docker image
	docker build -t cyberscribex:latest .

docker-run: ## Run with Docker Compose
	docker-compose up -d

docker-logs: ## View application logs
	docker-compose logs -f app

docker-stop: ## Stop Docker Compose
	docker-compose down

docker-clean: ## Stop and remove all containers and volumes
	docker-compose down -v

migrate: ## Run Flyway migrations
	./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3306/CyberScribeDB -Dflyway.user=root -Dflyway.password=

format: ## Format code
	./mvnw spotless:apply

clean: ## Clean build artifacts
	./mvnw clean
	 rm -rf target/
