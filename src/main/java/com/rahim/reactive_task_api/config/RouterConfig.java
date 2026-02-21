package com.rahim.reactive_task_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rahim.reactive_task_api.handler.TaskHandler;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.rahim.reactive_task_api.handler.StreamHandler;


@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> taskRoutes(TaskHandler handler) {
        return route()
            // Stats must be before /{id} to avoid being swallowed by the path variable
            .GET("/functional/tasks/stats", handler::getStats)

            // Basic CRUD
            .GET("/functional/tasks", handler::getAllTasks)
            .GET("/functional/tasks/{id}", handler::getTaskById)
            .POST("/functional/tasks", accept(MediaType.APPLICATION_JSON), handler::createTask)
            .PUT("/functional/tasks/{id}", accept(MediaType.APPLICATION_JSON), handler::updateTask)
            .DELETE("/functional/tasks/{id}", handler::deleteTask)

            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> streamRoutes(StreamHandler handler) {
        return route()
            .GET("/stream/tasks", handler::streamTasks)
            .GET("/stream/tasks/sse", handler::streamTasksWithSSE)
            .build();
    }
}
