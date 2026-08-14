package com.kineticos.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AiProviderConfigJpaRepository extends JpaRepository<AiProviderConfigJpa, Long> {

    List<AiProviderConfigJpa> findAllByOrderByProviderAsc();

    Optional<AiProviderConfigJpa> findByProvider(String provider);

    Optional<AiProviderConfigJpa> findByActiveTrue();

    @Modifying
    @Query("update AiProviderConfigJpa c set c.active = false where c.active = true")
    void deactivateAll();
}
