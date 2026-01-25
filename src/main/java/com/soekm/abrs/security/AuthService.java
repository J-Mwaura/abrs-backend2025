package com.soekm.abrs.security;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.dto.RegistrationRequest;
import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.entity.AppUser;
import com.soekm.abrs.entity.RefreshToken;
import com.soekm.abrs.entity.Role;
import com.soekm.abrs.entity.enums.RoleName;
import com.soekm.abrs.entity.enums.StaffType;
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
import java.util.HashSet;
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
        // 1. Find the user by phone (Standard for Mobile/Ionic apps)
        AppUser user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("User with phone " + request.getPhone() + " not found"));

        // 2. Security Check: Is this user allowed to use PIN login?
        if (!user.isUsePinLogin()) {
            throw new BadCredentialsException("PIN login is not enabled for this account");
        }

        // 3. Verify the PIN (Matches request raw PIN with DB BCrypt hash)
        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new BadCredentialsException("Invalid PIN");
        }

        // 4. Set Security Context (This tells Spring the user is now "Logged In")
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. Generate Response (Token + User details)
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

        // 1. Role & StaffType Resolution (Hardcoded to ATTENDANT)
        // Since request no longer provides it, we default here.
        StaffType defaultStaffType = StaffType.ATTENDANT;

        Role userRole = roleRepository.findByRoleName(RoleName.ATTENDANT)
                .orElseThrow(() -> new RoleNotFoundException("Error: Role 'ATTENDANT' not found."));

        // 2. Business Logic Validation
        if (repository.existsByUsername(request.username())) {
            throw new IllegalStateException("Username '" + request.username() + "' is already taken");
        }
        if (repository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email is already registered");
        }
        if (repository.existsByPhone(request.phone())) {
            throw new IllegalStateException("Phone number is already registered");
        }

        // 3. Unified Entity Creation
        // Note: Using the injected passwordEncoder bean
        AppUser appUser = AppUser.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .phone(request.phone())
                .staffType(defaultStaffType)
                .password(passwordEncoder.encode(request.password()))
                .pin(passwordEncoder.encode(request.pin()))
                .roles(new HashSet<>(Set.of(userRole))) // Use HashSet for JPA compatibility
                .usePinLogin(true)
                .build();

        repository.save(appUser);

        // 4. Response Building
        // No JWT is generated here because an Admin is performing this action
        Map<String, Object> data = new HashMap<>();
        data.put("user", appUser);

        return ResponseEntity.ok(ApiResponse.success(
                "Staff member " + appUser.getUsername() + " registered successfully as ATTENDANT",
                data
        ));
    }
}