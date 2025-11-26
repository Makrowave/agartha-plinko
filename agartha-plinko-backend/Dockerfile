# Build stage: Maven + Temurin‑21 (alpine)
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: OpenJDK 21 (slim)
FROM eclipse-temurin:21 AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]