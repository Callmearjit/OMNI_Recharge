# ─── Stage 1: Build ───────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app


# Install Maven directly
RUN apk add --no-cache maven

COPY pom.xml ./
RUN mvn dependency:go-offline -q   
COPY src ./src
RUN mvn package -DskipTests -q  


# ─── Stage 2: Run ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN mkdir -p logs

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
