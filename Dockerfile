FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the Maven descriptor first (for dependency download optimizations)
COPY pom.xml .

# Download dependencies (optional but can help layer caching)
RUN mvn dependency:go-offline

# Now copy the source code
COPY src ./src

# Build the application, compile & packaging into j.ar
RUN mvn -B package

# ──────────────────────────────
# 2) Runtime Stage
# ──────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the generated .jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]