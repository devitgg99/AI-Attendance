# ──────────────────────────────
# 1) Build Stage
# ──────────────────────────────
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# Copy Gradle configuration first (for dependency caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (helps with layer caching)
RUN ./gradlew dependencies --no-daemon || return 0

# Copy source code
COPY src ./src

# Build the application (creates fat jar if configured)
RUN ./gradlew clean build --no-daemon

# ──────────────────────────────
# 2) Runtime Stage
# ──────────────────────────────
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
