package com.Renthub;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "RentHub API is working! Time: " + System.currentTimeMillis();
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "✓ Server is healthy! Database connected!";
    }
}