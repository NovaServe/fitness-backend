package com.novaserve.fitness;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    @GetMapping("/api/v1/test")
    public ResponseEntity<?> getTestMessage() {
        Map<String, String> data = new HashMap<>();
        data.put("data", "Test message from the backend");
        return ResponseEntity.ok(data);
    }
}
