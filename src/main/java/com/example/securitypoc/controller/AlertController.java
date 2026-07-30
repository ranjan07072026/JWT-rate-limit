package com.example.securitypoc.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    @GetMapping
    public List<Map<String, Object>> alerts(Authentication authentication) {
        return List.of(Map.of(
                "alertId", "ALT-1001",
                "deviceId", "RTR-101",
                "severity", "CRITICAL",
                "status", "OPEN",
                "requestedBy", authentication.getName(),
                "timestamp", Instant.now().toString()));
    }
}
