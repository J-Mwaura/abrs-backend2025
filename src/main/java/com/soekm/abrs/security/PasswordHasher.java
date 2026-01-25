package com.soekm.abrs.security;

import com.soekm.abrs.entity.AppUser;
import com.soekm.abrs.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class PasswordHasher implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        String username = "agent01";
        String rawPassword = "1234"; // the password you want to set

        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            String hashed = passwordEncoder.encode(rawPassword);
            user.setPassword(hashed);
            userRepository.save(user);
            System.out.println("Updated password for user: " + username);
            System.out.println("Hashed password: " + hashed);
        }, () -> {
            System.out.println("User not found: " + username);
        });
    }
}
