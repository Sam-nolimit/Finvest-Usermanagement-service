package com.prunny.auth.exception;

public class UploadFailedException extends RuntimeException {
    public UploadFailedException(String message) {
        super(message);
    }
}