# ---------- Stage 1: Build the Spring Boot JAR ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven configuration and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy all source files and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the built JAR ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port that the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]