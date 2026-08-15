package com.micoach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del monolito modular.
 * El escaneo de componentes parte de {@code com.micoach}, por lo que
 * todos los módulos ({@code com.micoach.auth}, {@code com.micoach.user}, ...)
 * se descubren automáticamente.
 */
@SpringBootApplication
public class MiCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiCoachApplication.class, args);
    }
}
