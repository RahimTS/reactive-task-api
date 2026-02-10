package com.rahim.reactive_task_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;
import com.rahim.reactive_task_api.service.TaskService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks
    @GetMapping 
    public Flux<Task> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        return status != null
            ? taskService.getTasksByStatus(status)
            : taskService.getAllTasks();
    }

    // Get /api/tasks/stats
    @GetMapping("/stats")
    public Mono<?> getStats() {
        return taskService.getStats();
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public Mono<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    // POST /api/tasks
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> createTask(@Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public Mono<Task> updateTask(@PathVariable String id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    // DELETE /api/task/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTask(@PathVariable String id) {
        return taskService.deleteTask(id);
    }
}
