package com.rahim.reactive_task_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rahim.reactive_task_api.handler.TaskHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> taskRoutes(TaskHandler handler) {
        return route()
            // Basic CRUD
            .GET("/functional/tasks", handler::getAllTasks)
            .GET("/functional/tasks/{id}", handler::getTaskById)
            .POST("/functional/tasks", accept(MediaType.APPLICATION_JSON), handler::createTask)
            .PUT("/functional/tasks/{id}", accept(MediaType.APPLICATION_JSON), handler::updateTask)
            .DELETE("/functional/tasks/{id}", handler::deleteTask)
            
            // Stats
            .GET("/functional/tasks/stats", handler::getStats)
            
            .build();
    }
}
