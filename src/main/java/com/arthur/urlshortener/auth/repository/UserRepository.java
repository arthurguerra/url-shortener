package com.arthur.urlshortener.auth.repository;

import com.arthur.urlshortener.auth.entity.Email;
import com.arthur.urlshortener.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailValueIgnoreCase(String email);
    Optional<User> findByEmail(Email email);
}
