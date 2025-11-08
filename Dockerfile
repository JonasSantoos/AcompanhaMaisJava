#Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

#Copia apenas os arquivos necessários primeiro (cache otimizado)
COPY pom.xml .
RUN mvn dependency:go-offline -B

#Agora copia o código-fonte
COPY src ./src

#Compila o projeto
RUN mvn clean package -DskipTests

#Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /work

#Copia os artefatos gerados do build
COPY --from=build /app/target/quarkus-app/lib/ /work/lib/
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app/ /work/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080

#Define o comando de execução
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]