package com.prunny.auth.exception;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(String message) {
        super(message);
        System.out.println(message+ "messsage");
    }
}


