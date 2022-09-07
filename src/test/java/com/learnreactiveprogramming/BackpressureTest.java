package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

@Slf4j
public class BackpressureTest {

    @Test
    void testBackPressure() {
        var numberRange = Flux.range(1, 100).log();

        numberRange
                .onBackpressureDrop(i-> System.out.println("Drop element " + i))
                .subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
               request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Value is " + value);
//                if (value % 2 == 0 || value < 50) {
//                    request(2);
//                }else{
//                    cancel();
//                }
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("Cancelled");
            }
        });
    }
}
