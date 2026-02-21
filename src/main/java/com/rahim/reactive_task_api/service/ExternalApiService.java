package com.rahim.reactive_task_api.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    private final WebClient webClient;

    public ExternalApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    }

    public Flux<Post> getAllPosts() {
        return webClient.get()
            .uri("/posts")
            .retrieve()
            .bodyToFlux(Post.class)
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(e -> {
                System.err.println("Error fetching posts: " + e.getMessage());
                return Flux.empty();
            });
    }

    public Mono<Post> getPostById(Long id) {
        return webClient.get()
            .uri("/posts/{id}", id)
            .retrieve()
            .bodyToMono(Post.class)
            .timeout(Duration.ofSeconds(3))
            .retry(2);
    }

    public Mono<Post> createPost(Post post) {
        return webClient.post()
            .uri("/posts")
            .bodyValue(post)
            .retrieve()
            .bodyToMono(Post.class);
    }

    record Post(Long id, Long userId, String title, String body) {}
}
