package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public Flux<String> explore_PublishOn() {
        var nameFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase).log();

        var nameFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase).log();
        return nameFlux.mergeWith(nameFlux1);
    }

    public Flux<String> explore_SubscribeOn() {
        var nameFlux = Flux.fromIterable(namesList)
                .map(s -> {
                    log.info("Name is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.parallel())
                .map(this::upperCase).log();

        var nameFlux1 = Flux.fromIterable(namesList1)
                .map(s -> {
                    log.info("Name is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.parallel())
                .map(this::upperCase).log();
        return nameFlux.mergeWith(nameFlux1);
    }
}
