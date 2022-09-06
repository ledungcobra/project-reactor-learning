package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();
    }

    public Flux<String> namesFluxMap() {
        return namesFlux().map(String::toUpperCase).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("adam");
    }

    public Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFlux_Map(int strLength) {
        return namesFlux()
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .map(s -> s.length() + " - " + s);
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
    }

    public Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .flatMap(s -> Mono.just(s.toUpperCase()))
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay).log();
    }

    public Flux<String> namesFlux_concatMap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .flatMap(s -> Mono.just(s.toUpperCase()))
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitStringWithDelay).log();
    }

    public Flux<String> splitStringWithDelay(String name) {
        var rng = new Random();
        return Flux.fromArray(name.split(""))
                .delayElements(Duration.ofMillis(rng.nextInt(300)));
    }

    private void performanceFlatmapAndConcatMap() throws InterruptedException {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        var countDown = new CountDownLatch(2);
        var startTime = System.currentTimeMillis();
        long finalStartTime = startTime;
        namesFlux.flatMap(this::splitStringWithDelay)
                .doOnComplete(() -> {
                    System.out.println("Flatmap: " + (System.currentTimeMillis() - finalStartTime));
                    countDown.countDown();
                })
                .subscribe();
        startTime = System.currentTimeMillis();
        long finalStartTime1 = startTime;
        namesFlux.concatMap(this::splitStringWithDelay).doOnComplete(() -> {
            System.out.println("ConcatMap: " + (System.currentTimeMillis() - finalStartTime1));
            countDown.countDown();
        }).subscribe();
        countDown.await();
    }

    public Mono<List<String>> namesMono_flatmap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono);
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString);
    }

    public Flux<String> namesFlux_Transform(int minCharLength) {
        Function<Flux<String>, Flux<String>> filter = strings -> strings
                .map(String::toUpperCase)
                .filter(s -> s.length() > minCharLength);
        return namesFlux().transform(filter);
    }

    private String curThreadName() {
        return Thread.currentThread().getName();
    }

    private Mono<List<String>> splitStringMono(String s) {
        System.out.println(curThreadName());
        return Mono.just(List.of(s.split("")));
    }

    public Flux<String> exploreConcatWith() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        var namesFlux2 = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        return namesFlux.concatWith(namesFlux2);
    }

    public Flux<String> exploreConcat() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        var namesFlux2 = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        return Flux.concat(namesFlux, namesFlux2);
    }

    public void compareMergeAndConcat() {
        var namesFlux = Flux.fromIterable(List.of("A", "B", "C")).delayElements(Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("D", "E", "F")).delayElements(Duration.ofMillis(120));
        // Asynchronous
        Flux.merge(namesFlux, namesFlux2).subscribe(e -> System.out.println("merge: " + e));
        // Waiting for the first flux to complete and then executing the second flux
        Flux.concat(namesFlux, namesFlux2).subscribe(e -> System.out.println("concat: " + e));
    }

    public Flux<String> merge() {
        var namesFlux = Flux.fromIterable(List.of("A", "B", "C")).delayElements(Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("D", "E", "F")).delayElements(Duration.ofMillis(120));
        return Flux.merge(namesFlux, namesFlux2);
    }

    // Merge eagerly subscribe to all fluxes and return combined flux
    public Flux<String> mergeSequential() {
        var namesFlux = Flux.fromIterable(List.of("A", "B", "C")).delayElements(Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("D", "E", "F")).delayElements(Duration.ofMillis(120));
        return Flux.mergeSequential(namesFlux, namesFlux2);
    }

    public Flux<String> zip() {
        var namesFlux = Flux.fromIterable(List.of("A", "B", "C")).delayElements(Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("D", "E", "F")).delayElements(Duration.ofMillis(120));
        return Flux.zip(namesFlux, namesFlux2)

                .map(t2 -> t2.getT1() + t2.getT2());
    }

    public Flux<String> exception_flux() {
        return Flux.just("A", "B", "C").concatWith(Flux.error(new RuntimeException("Exception occur")))
                .concatWith(Flux.just("D"));

    }

    public Flux<String> onErrorReturn() {
        return exception_flux().onErrorReturn("default");
    }

    public Flux<String> onErrorResume() {
        var recoverFlux = Flux.just("D", "E", "F");
        return exception_flux()
                .onErrorResume((err) -> {
                    System.out.println("Error: " + err);
                    return recoverFlux;
                });
    }

    public Flux<String> onErrorContinue() {
        return Flux.just("A", "B", "C").map(s -> {
            if (s.equals("B")) {
                throw new IllegalStateException("B is not allowed");
            }
            return s;
        }).onErrorContinue((err, obj) -> {
            System.out.println("Error: " + err + " for " + obj);
        });
    }

    public Flux<String> onErrorMap() {
        return Flux.just("A", "B", "C").map(s -> {
            if (s.equals("B")) {
                throw new IllegalStateException("B is not allowed");
            }
            return s;
        }).onErrorMap((err) -> new ReactorException(err, err.getMessage()));
    }

    public Flux<String> doOnError(){
        return exception_flux().doOnError(err -> System.out.println("Error: " + err));
    }

    public static void main(String[] args) {
        var service = new FluxAndMonoGeneratorService();
        service.onErrorMap().subscribe(System.out::println);
    }
}
