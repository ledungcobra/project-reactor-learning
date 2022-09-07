package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MovieReactiveService {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;
    private final RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }


    public Flux<Movie> getAllMovies() {
        Flux<MovieInfo> moviesInfo = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfo.flatMap(m -> {
            log.info("Flat map call");
            Mono<List<Review>> review = reviewService.retrieveReviewsFlux(m.getMovieInfoId()).collect(Collectors.toList());
            return review.map(reviewList -> new Movie(m, reviewList));
        }).onErrorMap((err) -> {
            log.info("Error is {}", err);
            throw new MovieException(err.getMessage());
        }).log();
    }

    public Flux<Movie> getAllMovies_retry() {
        Flux<MovieInfo> moviesInfo = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfo.flatMap(m -> {
                    log.info("Running flat map");
                    Mono<List<Review>> review = reviewService.retrieveReviewsFlux(m.getMovieInfoId()).collect(Collectors.toList());
                    return review.map(reviewList -> new Movie(m, reviewList));
                }).onErrorMap((err) -> {
                    throw new MovieException(err.getMessage());
                })
                .retry(3)
                .log();
    }

    public Flux<Movie> getAllMovies_retryWhen() {
        Flux<MovieInfo> moviesInfo = this.movieInfoService.retrieveMoviesFlux();
        var retry = Retry.backoff(3, Duration.ofMillis(300))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new MovieException(retrySignal.failure().getMessage()));
        return moviesInfo.flatMap(m -> {
                    log.info("Running flat map");
                    Mono<List<Review>> review = reviewService.retrieveReviewsFlux(m.getMovieInfoId()).collect(Collectors.toList());
                    return review.map(reviewList -> new Movie(m, reviewList));
                }).onErrorMap((err) -> {
                    throw new MovieException(err.getMessage());
                })
                .retryWhen(retry)
                .log()
                ;
    }

    public Flux<Movie> getAllMovies_repeat() {
        Flux<MovieInfo> moviesInfo = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfo.flatMap(m -> {
                    log.info("Running flat map");
                    Mono<List<Review>> review = reviewService.retrieveReviewsFlux(m.getMovieInfoId()).collect(Collectors.toList());
                    return review.map(reviewList -> new Movie(m, reviewList));
                }).onErrorMap((err) -> {
                    throw new MovieException(err.getMessage());
                })
                .repeat()
                .log()
                ;
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movie = this.movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviews = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movie.zipWith(reviews).map(t2 -> new Movie(t2.getT1(), t2.getT2()));
    }

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        var movie = this.movieInfoService.retrieveMovieInfoMonoUsingId(movieId).log();
        var reviews = reviewService.retrieveReviewsFlux(movieId).collectList().log();
        var revenue = Mono.fromCallable(() -> revenueService.getRevenue(movieId)).subscribeOn(Schedulers.boundedElastic()).log();

        return Mono.zip(movie, reviews, revenue).map(t3 -> new Movie(t3.getT1(), t3.getT2(), t3.getT3()));
    }
}
