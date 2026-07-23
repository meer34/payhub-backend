package com.mir.payhub.user.repository;

import com.mir.payhub.common.enums.RoleType;
import com.mir.payhub.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleType name);

}