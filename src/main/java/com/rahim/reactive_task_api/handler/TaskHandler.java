package com.rahim.reactive_task_api.handler;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;
import com.rahim.reactive_task_api.service.TaskService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final TaskService taskService;

    // GET /tasks
    public Mono<ServerResponse> getAllTasks(ServerRequest request) {
        String statusParam = request.queryParam("status").orElse(null);

        if(statusParam != null) {
            TaskStatus status = TaskStatus.valueOf(statusParam.toUpperCase());
            return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.getTasksByStatus(status), Task.class);
        }

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(taskService.getAllTasks(), Task.class)
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    // GET /tasks/{id}
    public Mono<ServerResponse> getTaskById(ServerRequest request) {
        String id = request.pathVariable("id");

        return taskService.getTaskById(id)
            .flatMap(task -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    // POST /tasks
    public Mono<ServerResponse> createTask(ServerRequest request) {
        return request.bodyToMono(Task.class)
            .flatMap(taskService::createTask)
            .flatMap(task -> ServerResponse.created(URI.create("/tasks/" + task.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task));
    }

    // PUT /tasks/{id}
    public Mono<ServerResponse> updateTask(ServerRequest request) {
        String id = request.pathVariable("id");
        
        return request.bodyToMono(Task.class)
            .flatMap(task -> taskService.updateTask(id, task))
            .flatMap(task -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    
    // DELETE /tasks/{id}
    public Mono<ServerResponse> deleteTask(ServerRequest request) {
        String id = request.pathVariable("id");
        
        return taskService.deleteTask(id)
            .then(ServerResponse.noContent().build())
            .onErrorResume(e -> ServerResponse.notFound().build());
    }
    
    // GET /tasks/stats
    public Mono<ServerResponse> getStats(ServerRequest request) {
        return taskService.getStats()
            .flatMap(stats -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stats));
    }
}
