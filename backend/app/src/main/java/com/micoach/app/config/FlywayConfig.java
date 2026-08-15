package com.micoach.app.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    /**
     * Repara los checksums de migraciones ya aplicadas antes de migrar. El rename del
     * proyecto (KineticOs -> MiCoach) tocó comentarios de migraciones que ya habían
     * corrido contra la base, y el equipo no siempre tiene acceso directo a ella para
     * correr `flyway repair` a mano. repair() solo actualiza flyway_schema_history,
     * nunca toca datos ni schema; si los checksums ya coinciden, es un no-op.
     */
    @Bean
    public FlywayMigrationStrategy repairBeforeMigrateStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
