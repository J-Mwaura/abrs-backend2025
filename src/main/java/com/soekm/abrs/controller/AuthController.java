package com.soekm.abrs.controller;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author James Mwaura
 * 2026
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- Password login endpoint ---
    @PostMapping("/login/password")
    public ResponseEntity<?> loginWithPassword(
            @RequestBody PasswordLoginRequest request) {

        return ResponseEntity.ok(authService.authenticateWithPassword(request));
    }

    // --- PIN login endpoint ---
    @PostMapping("/login/pin")
    public ResponseEntity<?> loginWithPin(
            @RequestBody PinLoginRequest request) {

        return ResponseEntity.ok(authService.authenticateWithPin(request));
    }
}