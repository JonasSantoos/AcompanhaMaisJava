# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first (for better caching)
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -Dquarkus.package.type=uber-jar -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the JAR file
COPY --from=build /app/target/*-runner.jar app.jar

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]