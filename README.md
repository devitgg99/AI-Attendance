# Attendance Management System - Backend API

A Spring Boot REST API for managing attendance, user sessions, and permissions.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL** (Primary Database)
- **H2 Database** (Development/Testing)
- **Gradle** (Build Tool)
- **Lombok** (Code Generation)

## Project Structure

```
src/main/java/com/example/attendancemanagement/
├── AttendanceManagementApplication.java    # Main Spring Boot Application
├── config/
│   └── SecurityConfig.java                # Security Configuration
├── controller/
│   └── UserController.java                # REST Controllers
├── entity/
│   ├── BaseEntity.java                    # Base Entity with audit fields
│   ├── UserInfo.java                      # User entity
│   ├── UserSession.java                   # User session entity
│   ├── Attendance.java                    # Attendance entity
│   └── Permission.java                    # Permission entity
├── enums/
│   ├── UserRole.java                      # User role enum (ADMIN, STUDENT)
│   ├── AttendanceStatus.java              # Attendance status enum
│   └── Shift.java                         # Shift enum
└── repository/
    ├── UserInfoRepository.java            # User repository
    ├── UserSessionRepository.java         # Session repository
    ├── AttendanceRepository.java          # Attendance repository
    └── PermissionRepository.java          # Permission repository
```

## Database Schema

### User Info Table

- `user_id` (UUID, Primary Key)
- `email` (VARCHAR 50, Unique)
- `password` (TEXT)
- `user_role` (ENUM: ADMIN, STUDENT)
- `pin` (VARCHAR 6)
- `profile` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### User Session Table

- `session_id` (UUID, Primary Key)
- `user_id` (UUID, Foreign Key to UserInfo)
- `refresh_token` (TEXT)
- `fcm_token` (TEXT)
- `device_id` (VARCHAR 255, Nullable for ADMIN)
- `is_actived` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Attendance Table

- `attendance_id` (UUID, Primary Key)
- `user_id` (UUID, Foreign Key to UserInfo)
- `attendance_status` (ENUM: PRESENT, ABSENCE, LATE, OVERTIME)
- `checkin_at` (TIME)
- `checkout_at` (TIME)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Permission Table

- `permission_id` (UUID, Primary Key)
- `user_id` (UUID, Foreign Key to UserInfo)
- `reason` (TEXT)
- `shift` (ENUM: MORNING, AFTERNOON, FULLDAY)
- `is_approved` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Setup Instructions

### Prerequisites

- Java 17 or higher
- PostgreSQL (or use H2 for development)
- Gradle 8.5 or higher

### Database Setup

1. **PostgreSQL Setup:**

   ```sql
   CREATE DATABASE attendance_db;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE attendance_db TO postgres;
   ```

2. **Update application.yml** with your database credentials if needed.

### Running the Application

1. **Clone and navigate to the project directory:**

   ```bash
   cd backend
   ```

2. **Build the project:**

   ```bash
   ./gradlew build
   ```

3. **Run the application:**

   ```bash
   ./gradlew bootRun
   ```

4. **Access the application:**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console` (for development)
   - Actuator Health: `http://localhost:8080/actuator/health`

## API Endpoints

### User Management

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `POST /api/users` - Create new user

## Features

- **JPA Entities** with proper relationships and constraints
- **Base Entity** with automatic audit fields (created_at, updated_at)
- **UUID Primary Keys** for all entities
- **Enum Support** for user roles, attendance status, and shifts
- **Repository Pattern** with Spring Data JPA
- **Basic Security Configuration**
- **H2 Database** support for development
- **Actuator** for monitoring and health checks

## Development Notes

- The application uses Hibernate for database operations
- All timestamps are automatically managed by Hibernate
- UUID generation is handled by Hibernate's UUIDGenerator
- The project follows Spring Boot best practices
- Lombok is used to reduce boilerplate code

## Next Steps

1. Implement JWT authentication
2. Add validation annotations
3. Create service layer
4. Add exception handling
5. Implement attendance tracking logic
6. Add permission approval workflow
7. Create comprehensive test suite




