package com.micoach.workout.domain;

import lombok.Getter;

/**
 * Catálogo de músculos agrupados por zona corporal (tabla workout_muscles).
 */
@Getter
public class Muscle {

    private final Long id;
    private final String code;
    private final String name;
    private final String muscleGroup;

    private Muscle(Long id, String code, String name, String muscleGroup) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    public static Muscle restore(Long id, String code, String name, String muscleGroup) {
        return new Muscle(id, code, name, muscleGroup);
    }
}
