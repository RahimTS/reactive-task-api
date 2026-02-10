# Reactive Task API

A reactive REST API for task management built with Spring Boot 4 and WebFlux.

## Tech Stack

- **Java 25** with **Spring Boot 4.0.2**
- **Spring WebFlux** (non-blocking, reactive)
- **Project Reactor** (Mono/Flux)
- **Lombok** for boilerplate reduction
- **Jakarta Validation** for request validation
- In-memory `ConcurrentHashMap` storage (no database required)

## Project Structure

```
src/main/java/com/rahim/reactive_task_api/
├── ReactiveTaskApiApplication.java
├── controller/
│   └── TaskController.java          # REST endpoints
├── model/
│   ├── Task.java                    # Task entity
│   └── TaskStatus.java              # TODO, IN_PROGRESS, DONE
├── repository/
│   └── TaskRepository.java          # In-memory reactive repository
├── service/
│   ├── TaskService.java             # Business logic
│   └── TaskStats.java               # Stats record
└── exception/
    ├── GlobalExceptionHandler.java  # Centralized error handling
    └── TaskNotFoundException.java
```

## Getting Started

### Prerequisites

- Java 25+
- Maven

### Run

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks?status=TODO` | Filter tasks by status |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |
| GET | `/api/tasks/stats` | Get task statistics |

### Request Body (POST/PUT)

```json
{
  "title": "Learn WebFlux",
  "description": "Master reactive programming",
  "status": "IN_PROGRESS"
}
```

**Status values:** `TODO`, `IN_PROGRESS`, `DONE`

### Example Responses

**Create task** - `201 Created`
```json
{
  "id": "f77522b9-8c1b-49f8-8085-496ce4f4f6d5",
  "title": "Learn WebFlux",
  "description": "Master reactive programming",
  "status": "IN_PROGRESS",
  "createdAt": "2026-02-10T14:33:20.125",
  "updatedAt": "2026-02-10T14:33:20.125"
}
```

**Get stats** - `200 OK`
```json
{
  "total": 3,
  "done": 1,
  "inProgress": 1,
  "todo": 1
}
```

**Validation error** - `400 Bad Request`
```json
{
  "status": 400,
  "timestamp": "2026-02-10T14:33:21.901",
  "errors": {
    "title": "Title is required"
  }
}
```

## Testing

Run the included test script against a running server:

```bash
bash test-api.sh
```
