package com.site.repository;

import com.site.models.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Integer> {
    Optional<SiteUser> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<SiteUser> findByEmail(String email);
    boolean existsByUsername(String username);
}
