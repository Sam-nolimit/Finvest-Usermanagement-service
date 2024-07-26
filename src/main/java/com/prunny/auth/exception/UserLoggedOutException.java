package com.prunny.auth.exception;
public class UserLoggedOutException extends RuntimeException {
    public UserLoggedOutException(String message) {
        super(message);
    }
}