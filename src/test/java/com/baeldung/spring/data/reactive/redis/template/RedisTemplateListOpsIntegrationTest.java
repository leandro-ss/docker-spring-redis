package com.baeldung.spring.data.reactive.redis.template;


import com.baeldung.spring.data.reactive.redis.Application;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisTemplateListOpsIntegrationTest {

    private static final String LIST_NAME = "demo_list";
    private static redis.embedded.RedisServer redisServer;

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    private ReactiveListOperations<String, String> reactiveListOps;
    
    @BeforeAll
    public static void startRedisServer() throws IOException {
        redisServer = new RedisServerBuilder().port(6379).setting("maxmemory 128M").build();
        redisServer.start();
    }
    
    @AfterAll
    public static void stopRedisServer() throws IOException {
        redisServer.stop();
    }

    @BeforeEach
    public void setup() {
        reactiveListOps = redisTemplate.opsForList();
    }

    @Test
    public void givenListAndValues_whenLeftPushAndLeftPop_thenLeftPushAndLeftPop() {
        Mono<Long> lPush = reactiveListOps.leftPushAll(LIST_NAME, "first", "second")
            .log("Pushed");

        StepVerifier.create(lPush)
            .expectNext(2L)
            .verifyComplete();

        Mono<String> lPop = reactiveListOps.leftPop(LIST_NAME)
            .log("Popped");

        StepVerifier.create(lPop)
            .expectNext("second")
            .verifyComplete();
    }

}
