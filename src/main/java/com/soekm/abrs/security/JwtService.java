package com.soekm.abrs.security;


import com.soekm.abrs.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public String generateToken(AppUser user) {
        return jwtTokenProvider.createToken(user);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}