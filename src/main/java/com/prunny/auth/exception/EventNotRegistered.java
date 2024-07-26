package com.prunny.auth.exception;

public class EventNotRegistered extends RuntimeException{
    public EventNotRegistered(String message) {
        super(message);
    }
}