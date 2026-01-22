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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // --- Password login (Standard Username/Password) ---
    public Map<String, Object> authenticateWithPassword(PasswordLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // In Spring Security, principal is usually the UserDetails object
            AppUser user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return buildAuthResponse(user);
        } catch (BadCredentialsException e) {
            log.error("Invalid password for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    // --- PIN login (Phone/PIN) ---
    public Map<String, Object> authenticateWithPin(PinLoginRequest request) {
        AppUser user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("User with phone " + request.getPhone() + " not found"));

        // Use passwordEncoder to match the encrypted PIN from the DB
        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new BadCredentialsException("Invalid PIN");
        }

        // Manually set authentication for the security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResponse(user);
    }

    // --- Unified Response Builder (Carwash Style) ---
    private Map<String, Object> buildAuthResponse(AppUser user) {
        String token = jwtTokenProvider.createToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken.getToken());
        response.put("tokenType", "Bearer");
        response.put("username", user.getUsername());
        response.put("user", user); // Includes full user details for Angular state
        response.put("roles", user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());

        return response;
    }
}