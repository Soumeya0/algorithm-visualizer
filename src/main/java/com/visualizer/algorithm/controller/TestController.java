package com.visualizer.algorithm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint(Authentication authentication) {
        return "Hello, " + authentication.getName() + "! This endpoint is protected — you sent a valid token.";
    }
}
