package com.micoach.user.infrastructure.persistence;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad JPA de la tabla {@code user_profiles}.
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "sex")
    private String sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "height_cm")
    private BigDecimal heightCm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "activity_level")
    private String activityLevel;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "equipment")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> equipment;

    @Column(name = "training_days_per_week")
    private Short trainingDaysPerWeek;

    @Column(name = "training_minutes")
    private Short trainingMinutes;

    @Column(name = "preferred_time")
    private String preferredTime;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "tdee_calories")
    private Integer tdeeCalories;

    @Column(name = "dietary_goal")
    private String dietaryGoal;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}