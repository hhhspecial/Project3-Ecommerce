package com.example.ecommerce.common;

import java.time.LocalDateTime;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;


@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    

}

