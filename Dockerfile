# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN mvn clean package -Dquarkus.package.type=uber-jar -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /work

# Copy the JAR file - CORREÇÃO AQUI
COPY --from=build /app/target/*-runner.jar app.jar

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]