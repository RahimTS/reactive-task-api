package com.rahim.reactive_task_api.handler;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.service.TaskService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StreamHandler {

    private final TaskService taskService;

    // Stream all tasks every 2 seconds
    public Mono<ServerResponse> streamTasks(ServerRequest request) {
        Flux<Task> taskStream = Flux.interval(Duration.ofSeconds(2))
                .flatMap(tick -> taskService.getAllTasks());

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(taskStream, Task.class);
    }

    // Stream with Server-sent events (with metadata)
    public Mono<ServerResponse> streamTasksWithSSE(ServerRequest request) {
        Flux<ServerSentEvent<Object>> eventStream = Flux.interval(Duration.ofSeconds(2))
                .flatMap(tick -> taskService.getStats()
                        .map(stats -> ServerSentEvent.builder()
                                .id(String.valueOf(tick))
                                .event("task-stats")
                                .data(stats)
                                .build()));

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventStream, ServerSentEvent.class);
    }
}
