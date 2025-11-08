# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage  
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/quarkus-app/ /app/
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]