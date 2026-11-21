package com.example.ecommerce.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @EntityGraph(attributePaths = {"shop", "category"})
    Page<Product> findByStatusAndDeletedAtIsNull(ProductStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"shop", "category"})
    Optional<Product> findByIdAndStatusAndDeletedAtIsNull(Long id, ProductStatus status);

    Optional<Product> findByIdAndShopIdAndDeletedAtIsNull(Long id, Long shopId);

    Page<Product> findByShopIdAndDeletedAtIsNull(Long shopId, Pageable pageable);

    Page<Product> findByShopIdAndStatusAndDeletedAtIsNull(Long shopId, ProductStatus status, Pageable pageable);

    

        @Query(value = "select p from Product p "
                + "where p.status = :status and p.deletedAt is null "
                + "and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%'))) "
                + "and (:categoryId is null or p.category.id = :categoryId) "
                + "and (:shopId is null or p.shop.id = :shopId) "
                + "order by "
                + "case when :sort = 'price_asc' then (select min(s.price) from ProductSku s where s.productId = p.id) end asc, "
                + "case when :sort = 'price_desc' then (select min(s.price) from ProductSku s where s.productId = p.id) end desc, "
                + "p.id desc",
                countQuery = "select count(p) from Product p "
                + "where p.status = :status and p.deletedAt is null "
                + "and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%'))) "
                + "and (:categoryId is null or p.category.id = :categoryId) "
                + "and (:shopId is null or p.shop.id = :shopId)")
        Page<Product> search(@Param("status") ProductStatus status,
                        @Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("shopId") Long shopId,
                        @Param("sort") String sort,
                        Pageable pageable);
        
}