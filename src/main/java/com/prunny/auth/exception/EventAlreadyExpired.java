package com.prunny.auth.exception;


public class EventAlreadyExpired extends RuntimeException {
    public EventAlreadyExpired(String message) {
        super(message);
    }

}