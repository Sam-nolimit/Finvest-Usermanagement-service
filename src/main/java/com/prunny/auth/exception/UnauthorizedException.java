package com.prunny.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UnauthorizedException extends Throwable {
    public UnauthorizedException(String message){
        super(message);
    }

}
