# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /work

# Copy the Quarkus application - CORREÇÃO AQUI
COPY --from=build /app/target/quarkus-app/ /work/

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]