package com.soekm.abrs.controller;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.dto.RegistrationRequest;
import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.security.iService.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegistrationRequest request) {
        return authService.register(request);
    }

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
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPin(@Valid @RequestBody PinLoginRequest request) {
        // No try-catch here. If it fails, GlobalExceptionHandler takes over.
        Map<String, Object> response = authService.authenticateWithPin(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    // Add refresh token endpoint if needed
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        // Extract refresh token from header and validate
        // This depends on your refresh token implementation
        return ResponseEntity.ok(Map.of("message", "Refresh token endpoint"));
    }
}