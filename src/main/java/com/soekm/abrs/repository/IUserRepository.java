package com.soekm.abrs.repository;

import com.soekm.abrs.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author James Mwaura
 * 2026
 */
@Repository
public interface IUserRepository extends JpaRepository<AppUser, Long> {

    // Used for the main login flow and PIN verification
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByPhone(String phone);
    Optional<AppUser> findByEmail(String email);

    // Helper methods for validation checks
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}