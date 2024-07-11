package com.prunny.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth-controller")
public class AuthController {

    @GetMapping("/welcome")
    public ResponseEntity<String> sayWelcome() {
        return ResponseEntity.ok("Hello, welcome to Prunny Technology");
    }
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello, welcome to Prunny Technology");
    }

    @GetMapping("/goodbye")
    public ResponseEntity<String> sayGoodbye() {
        return ResponseEntity.ok("Goodbye from Prunny Technology");
    }
}
