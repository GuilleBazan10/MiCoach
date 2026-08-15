package com.micoach.nutrition.infrastructure.persistence;

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

/**
 * Entidad JPA de la tabla {@code nutrition_recipes} (catálogo).
 */
@Entity
@Table(name = "nutrition_recipes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "meal_category", nullable = false)
    private String mealCategory;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "servings", nullable = false)
    private Short servings;

    @Column(name = "prep_time_min")
    private Short prepTimeMin;

    @Column(name = "cook_time_min")
    private Short cookTimeMin;

    @Column(name = "calories_per_serving")
    private BigDecimal caloriesPerServing;

    @Column(name = "protein_per_serving")
    private BigDecimal proteinPerServing;

    @Column(name = "carbs_per_serving")
    private BigDecimal carbsPerServing;

    @Column(name = "fat_per_serving")
    private BigDecimal fatPerServing;

    @Column(name = "fiber_per_serving")
    private BigDecimal fiberPerServing;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean aiGenerated;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
