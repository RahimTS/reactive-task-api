package com.rahim.reactive_task_api.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TaskRepository {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public TaskRepository() {
        // Seed data
        save(new Task("Setup project", "Initialize Spring WebFlux", TaskStatus.DONE)).subscribe();
        save(new Task("Write controllers", "Create REST endpoints", TaskStatus.IN_PROGRESS)).subscribe();
        save(new Task("Add tests", "Write unit tests", TaskStatus.TODO)).subscribe();
    }

    public Mono<Task> save(Task task) {
        return Mono.defer(() -> {
            if(task.getId() == null) { 
                task.setId(UUID.randomUUID().toString());
                task.setCreatedAt(LocalDateTime.now());
            }
            task.setUpdatedAt(LocalDateTime.now());
            tasks.put(task.getId(), task);

            return Mono.just(task).delayElement(Duration.ofMillis(100));
        });
    }

    public Mono<Task> findById(String id) {
        return Mono.defer(() -> {
            Task task = tasks.get(id);
            return task != null
                ? Mono.just(task).delayElement(Duration.ofMillis(50))
                : Mono.empty();
        });
    }

    public Flux<Task> findAll() {
        return Flux.defer(() ->
            Flux.fromIterable(tasks.values())
                .delayElements(Duration.ofMillis(10))
        );
    }

    public Flux<Task> findByStatus(TaskStatus status) {
        return findAll()
            .filter(task -> task.getStatus() == status);
    }

    public Mono<Void> deleteById(String id) {
        return Mono.defer(() -> {
            tasks.remove(id);
            return Mono.empty();
        }).delayElement(Duration.ofMillis(50)).then();
    } 

    public Mono<Boolean> existsById(String id) {
        return Mono.just(tasks.containsKey(id));
    }

    public Mono<Long> count() {
        return Mono.just((long) tasks.size());
    }
}
