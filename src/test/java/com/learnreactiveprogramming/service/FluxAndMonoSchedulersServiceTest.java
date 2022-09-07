package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {
    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explore_PublishOn() {
        var flux = fluxAndMonoSchedulersService.explore_PublishOn();
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();

    }

    @Test
    void explore_SubscribeOn() {
        var flux = fluxAndMonoSchedulersService.explore_SubscribeOn();
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();

    }
}