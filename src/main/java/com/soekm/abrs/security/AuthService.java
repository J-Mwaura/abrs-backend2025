package com.soekm.abrs.security;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.entity.AppUser;
import com.soekm.abrs.entity.RefreshToken;
import com.soekm.abrs.repository.IUserRepository;
import com.soekm.abrs.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author James Mwaura
 * 2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // --- Password login ---
    public Map<String, Object> authenticateWithPassword(PasswordLoginRequest request) {

        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return buildAuthResponse(user);
    }

    // --- PIN login ---
    public Map<String, Object> authenticateWithPin(PinLoginRequest request) {

        AppUser user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new BadCredentialsException("Invalid PIN");
        }

        return buildAuthResponse(user);
    }

    // --- Common JWT + response builder ---
    private Map<String, Object> buildAuthResponse(AppUser user) {
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return Map.of(
                "accessToken", token,
                "refreshToken", refreshToken.getToken(),
                "username", user.getUsername(),
                "roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }
}