package br.com.fiap.acompanha.infrastructure.config;

import br.com.fiap.acompanha.application.service.CuidadorServiceImpl;
import br.com.fiap.acompanha.application.service.PacienteCuidadorServiceImpl;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.domain.service.CuidadorService;
import br.com.fiap.acompanha.domain.service.PacienteCuidadorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class CuidadorServiceConfig {

    @Produces
    @ApplicationScoped
    public CuidadorService cuidadorService(
            CuidadorRepository cuidadorRepository,
            PacienteRepository pacienteRepository
    ) {
        return new CuidadorServiceImpl(cuidadorRepository, pacienteRepository);
    }

    @Produces
    @ApplicationScoped
    public PacienteCuidadorService pacienteCuidadorService(
            CuidadorRepository cuidadorRepository,
            PacienteRepository pacienteRepository
    ) {
        return new PacienteCuidadorServiceImpl(cuidadorRepository, pacienteRepository);
    }
}
