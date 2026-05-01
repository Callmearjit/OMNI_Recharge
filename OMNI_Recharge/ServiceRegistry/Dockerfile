# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -Dmaven.test.skip=true -q

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir -p logs
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
