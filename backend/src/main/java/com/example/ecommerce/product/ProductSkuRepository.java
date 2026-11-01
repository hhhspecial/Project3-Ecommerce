package com.example.ecommerce.product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    List<ProductSku> findByProductIdOrderByPriceAsc(Long productId);

    @Query("select s.product.id as productId, min(s.price) as minPrice "
            + "from ProductSku s where s.product.id in :productIds group by s.product.id")
    List<MinPrice> findMinPrices(@Param("productIds") List<Long> productIds);

    interface MinPrice {
        Long getProductId();

        BigDecimal getMinPrice();
    }
}