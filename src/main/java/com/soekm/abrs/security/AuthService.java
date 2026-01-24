package com.soekm.abrs.security;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.dto.RegistrationRequest;
import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.entity.AppUser;
import com.soekm.abrs.entity.RefreshToken;
import com.soekm.abrs.entity.Role;
import com.soekm.abrs.entity.enums.RoleName;
import com.soekm.abrs.exceptions.RoleNotFoundException;
import com.soekm.abrs.repository.IUserRepository;
import com.soekm.abrs.repository.RoleRepository;
import com.soekm.abrs.security.iService.IAuthService;
import com.soekm.abrs.security.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private  final IUserRepository repository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    // --- Password login (Standard Username/Password) ---
    @Override
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
    @Override
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

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid RegistrationRequest request) {
        // 1. Password/PIN Encoding
        var bCryptEncoder = new BCryptPasswordEncoder();

        // 2. Role Resolution (Will throw RoleNotFoundException if missing)
        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new RoleNotFoundException("Error: Role '" + RoleName.USER + "' is not found."));

        // 3. Business Logic Validation
        // Throwing IllegalStateException ensures your GlobalExceptionHandler sends a clean ApiResponse.error()
        if (repository.existsByUsername(request.firstName())) {
            throw new IllegalStateException("Username is already taken");
        }
        if (repository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email is already registered");
        }
        if (repository.existsByPhone(request.phone())) {
            throw new IllegalStateException("Phone number is already registered");
        }

        // 4. Unified Entity Creation
        AppUser appUser = AppUser.builder()
                .firstName(request.firstName())  // Removed the "get"
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .phone(request.phone())
                .staffType(request.staffType())
                .password(bCryptEncoder.encode(request.password()))
                .pin(bCryptEncoder.encode(request.pin()))
                .usePinLogin(true)
                .roles(Set.of(userRole))
                .build();

        repository.save(appUser);

        // 5. Response Building
        String jwtToken = jwtService.generateToken(appUser);

        Map<String, Object> data = new HashMap<>();
        data.put("token", jwtToken);
        data.put("user", appUser);

        return ResponseEntity.ok(ApiResponse.success("Registration successful", data));
    }
}