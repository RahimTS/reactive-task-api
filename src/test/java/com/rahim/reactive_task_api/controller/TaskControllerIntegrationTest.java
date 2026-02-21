package com.rahim.reactive_task_api.controller;

import com.rahim.reactive_task_api.model.Task;
import com.rahim.reactive_task_api.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TaskControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllTasks_ShouldReturnTasks() {
        webTestClient.get()
            .uri("/api/tasks")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Task.class)
            .hasSize(3);  // From seeded data
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        Task newTask = new Task("New Task", "Description", TaskStatus.TODO);

        webTestClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newTask)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Task.class)
            .value(task -> {
                Assertions.assertNotNull(task.getId());
                Assertions.assertEquals("New Task", task.getTitle());
            });
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        // First create a task
        Task newTask = new Task("Test", "Desc", TaskStatus.TODO);

        Task created = webTestClient.post()
            .uri("/api/tasks")
            .bodyValue(newTask)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Task.class)
            .returnResult()
            .getResponseBody();

        // Then fetch it
        webTestClient.get()
            .uri("/api/tasks/{id}", created.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Task.class)
            .value(task -> Assertions.assertEquals(created.getId(), task.getId()));
    }

    @Test
    void updateTask_ShouldUpdateAndReturn() {
        // Create task
        Task task = new Task("Original", "Desc", TaskStatus.TODO);
        Task created = webTestClient.post()
            .uri("/api/tasks")
            .bodyValue(task)
            .exchange()
            .expectBody(Task.class)
            .returnResult()
            .getResponseBody();

        // Update it
        created.setTitle("Updated");
        created.setStatus(TaskStatus.DONE);

        webTestClient.put()
            .uri("/api/tasks/{id}", created.getId())
            .bodyValue(created)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Task.class)
            .value(updated -> {
                Assertions.assertEquals("Updated", updated.getTitle());
                Assertions.assertEquals(TaskStatus.DONE, updated.getStatus());
            });
    }

    @Test
    void deleteTask_ShouldRemoveTask() {
        // Create task
        Task task = new Task("To Delete", "Desc", TaskStatus.TODO);
        Task created = webTestClient.post()
            .uri("/api/tasks")
            .bodyValue(task)
            .exchange()
            .expectBody(Task.class)
            .returnResult()
            .getResponseBody();

        // Delete it
        webTestClient.delete()
            .uri("/api/tasks/{id}", created.getId())
            .exchange()
            .expectStatus().isNoContent();

        // Verify it's gone
        webTestClient.get()
            .uri("/api/tasks/{id}", created.getId())
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createTask_WithInvalidData_ShouldReturnBadRequest() {
        Task invalidTask = new Task();  // Missing required fields

        webTestClient.post()
            .uri("/api/tasks")
            .bodyValue(invalidTask)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
