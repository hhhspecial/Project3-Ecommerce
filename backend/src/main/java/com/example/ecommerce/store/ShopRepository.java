package com.example.ecommerce.store;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByOwnerUserId(Long ownerUserId);

    boolean existsByOwnerUserId(Long ownerUserId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Page<Shop> findByStatus(ShopStatus status, Pageable pageable);
}