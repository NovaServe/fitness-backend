/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness;

import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    @GetMapping("/api/v1/test")
    public ResponseEntity<?> getTestMessage() {
        return ResponseEntity.ok(new HashMap<>() {
            {
                put("data", "Test message from the server");
            }
        });
    }
}
