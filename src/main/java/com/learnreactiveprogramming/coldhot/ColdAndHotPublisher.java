package com.learnreactiveprogramming.coldhot;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ColdAndHotPublisher {

    public void coldPublish() {
        var flux = Flux.range(1, 10);
        flux.subscribe(i -> log.info("Subscriber 1: {}", i));
        flux.subscribe(i -> log.info("Subscriber 2: {}", i));
    }
}
