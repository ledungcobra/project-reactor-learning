package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovieReactiveServiceTest {
    private MovieReactiveService movieReactiveService;

    @BeforeEach
    void beforeEach() {
        this.movieReactiveService = new MovieReactiveService(new MovieInfoService(), new ReviewService(), new RevenueService());
    }

    @Test
    void getAllMovies() {
        var movies = movieReactiveService.getAllMovies();
        StepVerifier.create(movies)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                }).assertNext(movie -> {
                    assertEquals("The Dark Knight", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                }).assertNext(movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                }).verifyComplete();
    }

    @Test
    void getMovieById() {
        var movie = movieReactiveService.getMovieById(10L);
        StepVerifier.create(movie)
                .assertNext(m -> {
                    assertEquals(10L, m.getMovie().getMovieInfoId());
                    assertEquals(2, m.getReviewList().size());
                }).verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        var movie = movieReactiveService.getMovieByIdWithRevenue(10L);
        StepVerifier.create(movie)
                .assertNext(m -> {
                    assertEquals(10L, m.getMovie().getMovieInfoId());
                    assertEquals(2, m.getReviewList().size());
                    assertNotNull(m.getRevenue());
                }).verifyComplete();
    }
}