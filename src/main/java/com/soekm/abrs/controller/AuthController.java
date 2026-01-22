package com.soekm.abrs.controller;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- Password login endpoint ---
    @PostMapping("/login/password")
    public ResponseEntity<?> loginWithPassword(@RequestBody PasswordLoginRequest request) {
        try {
            Map<String, Object> response = authService.authenticateWithPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Login failed",
                    "message", e.getMessage()
            ));
        }
    }

    // --- PIN login endpoint ---
    @PostMapping("/login/pin")
    public ResponseEntity<?> loginWithPin(@RequestBody PinLoginRequest request) {
        try {
            Map<String, Object> response = authService.authenticateWithPin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "PIN login failed",
                    "message", e.getMessage()
            ));
        }
    }

    // Add refresh token endpoint if needed
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        // Extract refresh token from header and validate
        // This depends on your refresh token implementation
        return ResponseEntity.ok(Map.of("message", "Refresh token endpoint"));
    }
}