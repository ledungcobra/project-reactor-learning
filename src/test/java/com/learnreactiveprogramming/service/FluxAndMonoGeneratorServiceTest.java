package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void nameFlux() {
        var nameMono = fluxAndMonoGeneratorService.nameMono();
        StepVerifier.create(nameMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_Map() {
        var length = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_Map(length);
        StepVerifier.create(namesFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void splitString() {
        var name = "Hello world";
        var namesFlux = fluxAndMonoGeneratorService.splitString(name);
        StepVerifier.create(namesFlux)
                .expectNextCount(name.length())
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        var length = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(length);
        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concat_async() {
        var length = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap_async(length);
        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }


    @Test
    void namesMono_flatMapMany() {
        var length = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatMapMany(length);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_Transform() {
        var length = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_Transform(length);
        StepVerifier.create(namesFlux)
                .expectNextCount(2)
                .verifyComplete();
    }


    @Test
    void merge() {
        var result = fluxAndMonoGeneratorService.merge();
        StepVerifier.create(result)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void zip() {
        var result = fluxAndMonoGeneratorService.zip();
        StepVerifier.create(result)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        var r = fluxAndMonoGeneratorService.exception_flux();
        StepVerifier.create(r)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception occur")
                .verify();
    }

    @Test
    void onErrorReturn() {
        var r = fluxAndMonoGeneratorService.onErrorReturn();
        StepVerifier.create(r)
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void onErrorResume() {
        var r = fluxAndMonoGeneratorService.onErrorResume();
        StepVerifier.create(r)
                .expectNext("A", "B", "C")
                .expectNext("D", "E", "F")
                .verifyComplete();
    }

    @Test
    void onErrorContinue() {
        var r = fluxAndMonoGeneratorService.onErrorContinue();
        StepVerifier.create(r)
                .expectNext("A", "C")
                .verifyComplete();
    }

    @Test
    void onErrorMap() {
        var r = fluxAndMonoGeneratorService.onErrorMap();
        StepVerifier.create(r)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void doOnError() {
        var r = fluxAndMonoGeneratorService.doOnError();
        StepVerifier.create(r)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }
}
