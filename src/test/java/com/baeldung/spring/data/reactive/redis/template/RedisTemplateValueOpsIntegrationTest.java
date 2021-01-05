package com.baeldung.spring.data.reactive.redis.template;


import java.io.IOException;
import java.time.Duration;

import com.baeldung.spring.data.reactive.redis.Application;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baeldung.spring.data.reactive.redis.model.Employee;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServerBuilder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisTemplateValueOpsIntegrationTest {
    
    private static redis.embedded.RedisServer redisServer;

    @Autowired
    private ReactiveRedisTemplate<String, Employee> redisTemplate;
    private ReactiveValueOperations<String, Employee> reactiveValueOps;
    
    @BeforeAll
    public static void startRedisServer() {
        redisServer = new RedisServerBuilder().port(6379).setting("maxmemory 256M").build();
        redisServer.start();
    }
    
    @AfterAll
    public static void stopRedisServer() {
        redisServer.stop();
    }

    @BeforeEach
    public void setup() {
        reactiveValueOps = redisTemplate.opsForValue();
    }

    @Test
    public void givenEmployee_whenSet_thenSet() {

        Mono<Boolean> result = reactiveValueOps.set("123", new Employee("123", "Bill", "Accounts"));

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    public void givenEmployeeId_whenGet_thenReturnsEmployee() {

        Mono<Employee> fetchedEmployee = reactiveValueOps.get("123");

        StepVerifier.create(fetchedEmployee)
            .expectNext(new Employee("123", "Bill", "Accounts"))
            .verifyComplete();
    }

    @Test
    public void givenEmployee_whenSetWithExpiry_thenSetsWithExpiryTime() throws InterruptedException {

        Mono<Boolean> result = reactiveValueOps.set("129", new Employee("129", "John", "Programming"), Duration.ofSeconds(1));

        Mono<Employee> fetchedEmployee = reactiveValueOps.get("129");

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();

        Thread.sleep(2000L);

        StepVerifier.create(fetchedEmployee)
            .expectNextCount(0L)
            .verifyComplete();
    }

}
