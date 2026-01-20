package com.soekm.abrs.controller;

import com.soekm.abrs.dto.LogoutRequest;
import com.soekm.abrs.dto.TokenRefreshRequest;
import com.soekm.abrs.dto.response.TokenRefreshResponse;
import com.soekm.abrs.entity.RefreshToken;
import com.soekm.abrs.security.JwtService;
import com.soekm.abrs.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // 1. Verify existing token
        RefreshToken oldToken = refreshTokenService.verifyExpiration(
                refreshTokenService.findByToken(requestRefreshToken)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"))
        );

        var user = oldToken.getUser();

        // 2. Delete the old refresh token (rotation)
        refreshTokenService.deleteByToken(oldToken);

        // 3. Create a new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        // 4. Generate new access token
        String newAccessToken = jwtService.generateToken(user);

        // 5. Return both new tokens
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken.getToken()));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        // Find the refresh token and delete it
        refreshTokenService.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenService::deleteByToken);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}
