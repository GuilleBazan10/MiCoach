package com.kineticos.nutrition.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entidad JPA de la tabla {@code nutrition_meal_plans}.
 */
@Entity
@Table(name = "nutrition_meal_plans")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "target_calories")
    private Integer targetCalories;

    @Column(name = "target_protein_g")
    private BigDecimal targetProteinG;

    @Column(name = "target_carbs_g")
    private BigDecimal targetCarbsG;

    @Column(name = "target_fat_g")
    private BigDecimal targetFatG;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean aiGenerated;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
