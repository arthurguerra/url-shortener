package com.arthur.urlshortener.link.repository;

import com.arthur.urlshortener.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID>, PagingAndSortingRepository<Link, UUID> {
    Optional<Link> findByShortCode(String shortCode);

    Boolean existsByShortCode(String shortCode);
}
