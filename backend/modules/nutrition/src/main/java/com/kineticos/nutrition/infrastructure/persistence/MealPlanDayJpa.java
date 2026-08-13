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

import java.time.LocalDate;

/**
 * Entidad JPA de la tabla {@code nutrition_meal_plan_days}.
 */
@Entity
@Table(name = "nutrition_meal_plan_days")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDayJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meal_plan_id", nullable = false)
    private Long mealPlanId;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;
}
