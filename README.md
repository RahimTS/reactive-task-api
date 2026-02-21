# Reactive Task API

A reactive REST API for task management built with Spring Boot 4 and Spring WebFlux. Demonstrates reactive programming patterns including functional routing, Server-Sent Events streaming, WebClient usage, and reactive error handling.

## Tech Stack

- **Java 25** with **Spring Boot 4.0.2**
- **Spring WebFlux** — non-blocking, reactive HTTP layer
- **Project Reactor** — `Mono`/`Flux` publishers
- **Lombok** — boilerplate reduction
- **Jakarta Validation** — request validation
- In-memory `ConcurrentHashMap` storage (no database required)

## Project Structure

```
src/main/java/com/rahim/reactive_task_api/
├── ReactiveTaskApiApplication.java
├── config/
│   └── RouterConfig.java            # Functional route definitions
├── controller/
│   └── TaskController.java          # Annotation-based REST endpoints
├── exception/
│   ├── GlobalExceptionHandler.java  # Centralized error responses
│   └── TaskNotFoundException.java
├── filter/
│   └── LoggingFilter.java           # Request/response logging WebFilter
├── handler/
│   ├── TaskHandler.java             # Functional handler for task routes
│   └── StreamHandler.java           # SSE / streaming handler
├── model/
│   ├── Task.java                    # Task entity
│   └── TaskStatus.java              # TODO | IN_PROGRESS | DONE
├── repository/
│   └── TaskRepository.java          # In-memory reactive repository
└── service/
    ├── ErrorHandlingExamples.java   # Reactor error-pattern reference
    ├── ExternalApiService.java      # WebClient usage examples
    ├── TaskService.java             # Business logic
    └── TaskStats.java               # Stats record (total/done/inProgress/todo)

src/main/resources/
├── application.yaml
└── static/
    └── stats.html                   # Live SSE stats dashboard
```

## Getting Started

### Prerequisites

- Java 25+
- Maven (wrapper included)

### Run

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.

---

## API Reference

### Annotation-based endpoints — `/api/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks?status=TODO` | Filter tasks by status |
| GET | `/api/tasks/{id}` | Get task by ID |
| GET | `/api/tasks/stats` | Get task statistics |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |

### Functional routing endpoints — `/functional/tasks`

Same operations as above, implemented using Spring WebFlux functional routing (`RouterFunction` + `HandlerFunction`) instead of `@RestController`.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/functional/tasks` | Get all tasks (optional `?status` filter) |
| GET | `/functional/tasks/{id}` | Get task by ID |
| GET | `/functional/tasks/stats` | Get task statistics |
| POST | `/functional/tasks` | Create a new task |
| PUT | `/functional/tasks/{id}` | Update a task |
| DELETE | `/functional/tasks/{id}` | Delete a task |

### Streaming endpoints — `/stream/tasks`

| Method | Endpoint | Content-Type | Description |
|--------|----------|--------------|-------------|
| GET | `/stream/tasks` | `text/event-stream` | Stream all tasks, refreshed every 2 seconds |
| GET | `/stream/tasks/sse` | `text/event-stream` | Stream task statistics as SSE with event metadata |

Open `http://localhost:8080/stats.html` to view a live statistics dashboard driven by the SSE endpoint.

---

## Request / Response Examples

### Create task — `POST /api/tasks`

**Request body:**
```json
{
  "title": "Learn WebFlux",
  "description": "Master reactive programming",
  "status": "IN_PROGRESS"
}
```

**Response — `201 Created`:**
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

**Status values:** `TODO` | `IN_PROGRESS` | `DONE`

### Get statistics — `GET /api/tasks/stats`

**Response — `200 OK`:**
```json
{
  "total": 3,
  "done": 1,
  "inProgress": 1,
  "todo": 1
}
```

### Validation error — `400 Bad Request`
```json
{
  "status": 400,
  "timestamp": "2026-02-10T14:33:21.901",
  "errors": {
    "title": "Title is required"
  }
}
```

### Not found — `404 Not Found`
```json
{
  "status": 404,
  "message": "Task not found",
  "timestamp": "2026-02-10T14:33:21.901"
}
```

---

## Key Concepts Demonstrated

| Concept | Where |
|---------|-------|
| `Mono` / `Flux` publishers | `TaskService`, `TaskRepository` |
| Functional routing | `RouterConfig`, `TaskHandler` |
| Server-Sent Events (SSE) | `StreamHandler`, `stats.html` |
| `WebFilter` logging | `LoggingFilter` |
| `WebClient` for external APIs | `ExternalApiService` |
| `StepVerifier` unit testing | `TaskServiceTest` |
| `WebTestClient` integration testing | `TaskControllerIntegrationTest` |
| Reactive error handling patterns | `ErrorHandlingExamples` |
| Global exception handling | `GlobalExceptionHandler` |

---

## Testing

### Run all tests

```bash
./mvnw test
```

### Unit tests — `TaskServiceTest`

Mocks `TaskRepository` with Mockito and asserts reactive streams using Reactor's `StepVerifier`.

### Integration tests — `TaskControllerIntegrationTest`

Starts the full application on a random port and exercises the HTTP layer end-to-end using `WebTestClient`.

### Manual test script

Run the included shell script against a running server:

```bash
bash test-api.sh
```
