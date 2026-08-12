package com.kineticos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del monolito modular.
 * El escaneo de componentes parte de {@code com.kineticos}, por lo que
 * todos los módulos ({@code com.kineticos.auth}, {@code com.kineticos.user}, ...)
 * se descubren automáticamente.
 */
@SpringBootApplication
public class KineticOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(KineticOsApplication.class, args);
    }
}
