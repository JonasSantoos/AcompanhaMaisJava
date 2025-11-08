package br.com.fiap.acompanha.infrastructure.config;

import br.com.fiap.acompanha.domain.repository.*;
import br.com.fiap.acompanha.infrastructure.persistence.*;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class DatabaseConfig {

    @Produces
    @ApplicationScoped
    public DatabaseConnection databaseConnection(AgroalDataSource dataSource) {
        System.out.println("Criando DatabaseConnection...");
        return new DatabaseConnectionImpl(dataSource);
    }

    @Produces
    @ApplicationScoped
    public PacienteRepository pacienteRepository(DatabaseConnection databaseConnection) {
        System.out.println("Criando PacienteRepository...");
        return new JdbcPacienteRepository(databaseConnection);
    }


    @Produces
    @ApplicationScoped
    public CuidadorRepository cuidadorRepository(DatabaseConnection databaseConnection) {
        System.out.println("Criando CuidadorRepository...");
        return new JdbcCuidadorRepository(databaseConnection);
    }
}