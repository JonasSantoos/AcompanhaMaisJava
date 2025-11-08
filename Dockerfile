# Etapa de build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# Etapa final
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*-runner.jar app.jar

CMD ["java", "-jar", "app.jar"]
