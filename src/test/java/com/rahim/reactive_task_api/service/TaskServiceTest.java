package com.rahim.reactive_task_api.service;

import com.rahim.reactive_task_api.exception.TaskNotFoundException;
import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;
import com.rahim.reactive_task_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task("Test Task", "Description", TaskStatus.TODO);
        testTask.setId("123");
    }

    @Test
    void createTask_ShouldSaveTask() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(testTask));

        // When
        Mono<Task> result = taskService.createTask(testTask);

        // Then
        StepVerifier.create(result)
            .expectNext(testTask)
            .verifyComplete();

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        // Given
        when(taskRepository.findById("123")).thenReturn(Mono.just(testTask));

        // When
        Mono<Task> result = taskService.getTaskById("123");

        // Then
        StepVerifier.create(result)
            .expectNext(testTask)
            .verifyComplete();
    }

    @Test
    void getTaskById_WhenNotExists_ShouldThrowException() {
        // Given
        when(taskRepository.findById("999")).thenReturn(Mono.empty());

        // When
        Mono<Task> result = taskService.getTaskById("999");

        // Then
        StepVerifier.create(result)
            .expectError(TaskNotFoundException.class)
            .verify();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.TODO);
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.DONE);
        when(taskRepository.findAll()).thenReturn(Flux.just(task1, task2));

        // When
        Flux<Task> result = taskService.getAllTasks();

        // Then
        StepVerifier.create(result)
            .expectNext(task1)
            .expectNext(task2)
            .verifyComplete();
    }

    @Test
    void updateTask_WhenExists_ShouldUpdateAndReturn() {
        // Given
        Task updatedTask = new Task("Updated", "New desc", TaskStatus.DONE);
        when(taskRepository.findById("123")).thenReturn(Mono.just(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(testTask));

        // When
        Mono<Task> result = taskService.updateTask("123", updatedTask);

        // Then
        StepVerifier.create(result)
            .assertNext(task -> {
                assert task.getTitle().equals("Updated");
                assert task.getStatus() == TaskStatus.DONE;
            })
            .verifyComplete();
    }

    @Test
    void deleteTask_WhenExists_ShouldDelete() {
        // Given
        when(taskRepository.existsById("123")).thenReturn(Mono.just(true));
        when(taskRepository.deleteById("123")).thenReturn(Mono.empty());

        // When
        Mono<Void> result = taskService.deleteTask("123");

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        verify(taskRepository, times(1)).deleteById("123");
    }

    @Test
    void getStats_ShouldCalculateCorrectly() {
        // Given
        when(taskRepository.count()).thenReturn(Mono.just(10L));
        when(taskRepository.findByStatus(TaskStatus.DONE)).thenReturn(Flux.just(testTask, testTask));
        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(Flux.just(testTask));
        when(taskRepository.findByStatus(TaskStatus.TODO)).thenReturn(Flux.just(testTask, testTask, testTask));

        // When
        Mono<TaskStats> result = taskService.getStats();

        // Then
        StepVerifier.create(result)
            .assertNext(stats -> {
                assert stats.total() == 10L;
                assert stats.done() == 2L;
                assert stats.inProgress() == 1L;
                assert stats.todo() == 3L;
            })
            .verifyComplete();
    }
}
