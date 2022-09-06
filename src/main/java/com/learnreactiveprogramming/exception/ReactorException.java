package com.learnreactiveprogramming.exception;

public class ReactorException extends RuntimeException {

    public ReactorException(String message) {
        super(message);
    }

    public ReactorException(Throwable exception, String message) {
            super(message, exception);
    }
}