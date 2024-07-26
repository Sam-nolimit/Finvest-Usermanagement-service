package com.prunny.auth.exception;

public class CustomUserAlreadyRegistered extends RuntimeException {
    public CustomUserAlreadyRegistered(String message) {
        super(message);
        System.out.println(message+ "messsage");
    }
}
