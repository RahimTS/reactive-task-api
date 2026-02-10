package com.rahim.reactive_task_api.service;

import org.springframework.stereotype.Service;

import com.rahim.reactive_task_api.exception.TaskNotFoundException;
import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;
import com.rahim.reactive_task_api.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Mono<Task> createTask(Task task) {
        return taskRepository.save(task);
    }

    public Mono<Task> getTaskById(String id) {
        return taskRepository.findById(id)
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found: " + id)));
    }

    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Flux<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Mono<Task> updateTask(String id, Task updatedTask) {
        return taskRepository.findById(id)
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found: " + id)))
            .flatMap(existingTask -> {
                existingTask.setTitle(updatedTask.getTitle());
                existingTask.setDescription(updatedTask.getDescription());
                existingTask.setStatus(updatedTask.getStatus());
                return taskRepository.save(existingTask);
            });
    }

    public Mono<Void> deleteTask(String id) {
        return taskRepository.existsById(id)
            .flatMap(exists -> {
                if(!exists) {
                    return Mono.error(new TaskNotFoundException("Task not found: " + id));
                }
                return taskRepository.deleteById(id);
            }); 
    }
    
    public Mono<TaskStats> getStats() {
        return taskRepository.count()
            .zipWith(taskRepository.findByStatus(TaskStatus.DONE).count())
            .zipWith(taskRepository.findByStatus(TaskStatus.IN_PROGRESS).count())
            .zipWith(taskRepository.findByStatus(TaskStatus.TODO).count())
            .map(tuple -> new TaskStats(
                tuple.getT1().getT1().getT1(),  // total
                tuple.getT1().getT1().getT2(),  // done
                tuple.getT1().getT2(),           // in_progress
                tuple.getT2()                    // todo
            ));
    }
}
