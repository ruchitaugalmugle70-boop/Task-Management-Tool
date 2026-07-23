# Multi-stage build for Spring Boot App
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY taskmanagementtool_b72/pom.xml ./
COPY taskmanagementtool_b72/src ./src
RUN mvn clean package -DskipTests

# Lightweight Java 17 Runtime environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 7878
ENV PORT=7878
ENTRYPOINT ["java", "-jar", "app.jar"]
