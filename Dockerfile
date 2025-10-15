# ===== Stage 1: Build the application =====
FROM gradle:8.9-jdk21 AS build
WORKDIR /app

# Copy Gradle build files
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle gradle

# Give permission to Gradle wrapper (if needed)
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the JAR (skip tests for faster build)
RUN ./gradlew clean bootJar -x test

# ===== Stage 2: Create the final lightweight image =====
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
