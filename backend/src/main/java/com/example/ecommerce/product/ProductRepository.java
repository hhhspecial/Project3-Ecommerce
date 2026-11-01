package com.example.ecommerce.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @EntityGraph(attributePaths = {"shop", "category"})
    Page<Product> findByStatusAndDeletedAtIsNull(ProductStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"shop", "category"})
    Optional<Product> findByIdAndStatusAndDeletedAtIsNull(Long id, ProductStatus status);
}