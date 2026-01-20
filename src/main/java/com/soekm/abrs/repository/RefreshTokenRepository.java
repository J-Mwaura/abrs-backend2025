package com.soekm.abrs.repository;


import com.soekm.abrs.entity.RefreshToken;
import com.soekm.abrs.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


/**
 * @author James Mwaura
 * 2026
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
}

