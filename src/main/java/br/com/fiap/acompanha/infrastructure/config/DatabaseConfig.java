package br.com.fiap.acompanha.infrastructure.config;

import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.HospitalRepository;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;

import br.com.fiap.acompanha.infrastructure.persistence.JdbcCuidadorRepository;
import br.com.fiap.acompanha.infrastructure.persistence.JdbcHospitalRepository;
import br.com.fiap.acompanha.infrastructure.persistence.JdbcPacienteRepository;

import br.com.fiap.acompanha.infrastructure.persistence.*;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseConfig {

    @ApplicationScoped
    public DatabaseConnection databaseConnection(AgroalDataSource dataSource) {
        return new DatabaseConnectionImpl(dataSource);
    }

    @ApplicationScoped
    public PacienteRepository pacienteRepository(DatabaseConnection databaseConnection) {
        return new JdbcPacienteRepository(databaseConnection);
    }


    @ApplicationScoped
    public HospitalRepository hospitalRepository(DatabaseConnection databaseConnection) {
        return new JdbcHospitalRepository(databaseConnection);
    }

    @ApplicationScoped
    public CuidadorRepository CuidadorRepository(DatabaseConnection databaseConnection) {
        return new JdbcCuidadorRepository(databaseConnection);
    }

}