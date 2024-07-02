package com.prunny.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth-controller")
public class AuthController {
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello welcome to prunny Technology");
    }

}
