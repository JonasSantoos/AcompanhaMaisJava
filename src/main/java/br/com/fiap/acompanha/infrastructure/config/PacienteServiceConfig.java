package br.com.fiap.acompanha.infrastructure.config;

import br.com.fiap.acompanha.application.service.PacienteServiceImpl;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.domain.service.PacienteService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PacienteServiceConfig {

    @ApplicationScoped
    public PacienteService pacienteService(PacienteRepository pacienteRepository){
        return new PacienteServiceImpl(pacienteRepository);
    }
}
