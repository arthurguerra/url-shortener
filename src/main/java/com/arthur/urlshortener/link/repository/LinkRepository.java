package com.arthur.urlshortener.link.repository;

import com.arthur.urlshortener.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    Optional<Link> findByShortCode(String shortCode);
}
