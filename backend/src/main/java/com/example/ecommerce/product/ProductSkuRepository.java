package com.example.ecommerce.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    List<ProductSku> findByProductIdOrderByPriceAsc(Long productId);

    boolean existsByProductId(Long productId);

    @Query("select s.productId as productId, min(s.price) as minPrice "
            + "from ProductSku s where s.productId in :productIds group by s.productId")
    List<MinPrice> findMinPrices(@Param("productIds") List<Long> productIds);

    boolean existsBySkuCode(String skuCode);

    boolean existsBySkuCodeAndIdNot(String skuCode, Long id);

    Optional<ProductSku> findByIdAndProductId(Long id, Long productId);

    long countByProductId(Long productId);
    

    interface MinPrice {
        Long getProductId();

        BigDecimal getMinPrice();
    }
}