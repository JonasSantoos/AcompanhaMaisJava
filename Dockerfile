FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
<<<<<<< HEAD
COPY . .
=======

# Copy Maven files
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application using the pre-installed Maven
>>>>>>> 33bd74872555e3566ba1e69c804940739ee7e79a
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/quarkus-app/ /app/
EXPOSE 8080
<<<<<<< HEAD
CMD ["java", "-jar", "quarkus-run.jar"]
=======

ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]
>>>>>>> 33bd74872555e3566ba1e69c804940739ee7e79a
