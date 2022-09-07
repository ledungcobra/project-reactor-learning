package com.learnreactiveprogramming;

import com.learnreactiveprogramming.coldhot.ColdAndHotPublisher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class ColdAndHotPublisherTest {

    private ColdAndHotPublisher coldAndHotPublisher = new ColdAndHotPublisher();

    @Test
    void coldPublisherTest() {
        coldAndHotPublisher.coldPublish();
    }

    @Test
    void hotPublisher() {
        var flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));
        var connectableFlux = flux.publish();
        connectableFlux.connect();
        connectableFlux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 1: " + i));
        delay(2000);
        connectableFlux
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 2: " + i));
        delay(11000);
    }

    @Test
    void hotPublisherAutoConnect() {
        var flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));
        var connectableFlux = flux.publish().autoConnect(2);
        connectableFlux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 1: " + i));
        delay(2000);
        connectableFlux
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 2: " + i));
        delay(2000);
        connectableFlux
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 3: " + i));
        delay(11000);
        delay(2000);
    }

    @Test
    void hotPublisherAutoConnect_refCount() {
        // Stop emitting new event after 2 subscribers
        var flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));
        var connectableFlux = flux.publish().refCount(2);
        var disposable1 = connectableFlux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 1: " + i));
        delay(2000);
        var disposable2 = connectableFlux
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 2: " + i));
        delay(2000);
        disposable1.dispose();
        disposable2.dispose();
        connectableFlux
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> log.info("Subscriber 3: " + i));
        delay(11000);
        delay(2000);
    }
}
