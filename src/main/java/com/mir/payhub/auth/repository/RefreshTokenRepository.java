package com.mir.payhub.auth.repository;

import com.mir.payhub.auth.entity.RefreshToken;
import com.mir.payhub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);

}