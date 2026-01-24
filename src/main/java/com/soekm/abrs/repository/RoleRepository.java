package com.soekm.abrs.repository;

import com.soekm.abrs.entity.Role;
import com.soekm.abrs.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
	Optional<Role> findByRoleName(RoleName roleName);
}
