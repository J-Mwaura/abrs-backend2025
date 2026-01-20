package com.soekm.abrs.repository;

import com.soekm.abrs.entity.Role;
import com.soekm.abrs.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author James Mwaura
 * 2026
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    // Since your Role entity uses the RoleName Enum,
    // we use that for the lookup to ensure type safety.
    Optional<Role> findByRoleName(RoleName roleName);
}