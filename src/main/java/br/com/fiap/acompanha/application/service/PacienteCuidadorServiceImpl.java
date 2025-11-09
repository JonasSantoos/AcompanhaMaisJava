package br.com.fiap.acompanha.application.service;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.domain.service.PacienteCuidadorService;

import java.util.ArrayList;

public class PacienteCuidadorServiceImpl implements PacienteCuidadorService {

    private final CuidadorRepository cuidadorRepository;
    private final PacienteRepository pacienteRepository;

    public PacienteCuidadorServiceImpl(CuidadorRepository cuidadorRepository, PacienteRepository pacienteRepository) {
        this.cuidadorRepository = cuidadorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public void vincular(String cpfCuidador, String cpfPaciente) {
        try {
            Cuidador cuidador = cuidadorRepository.buscarPorCpf(cpfCuidador);
            Paciente paciente = pacienteRepository.buscarPorCpf(cpfPaciente);

            if (cuidador.getPacientes() == null) {
                cuidador.setPacientes(new ArrayList<>());
            }

            if (!cuidador.getPacientes().contains(paciente)) {
                cuidador.getPacientes().add(paciente);
                paciente.setCuidador(cuidador);
                cuidadorRepository.editar(cuidador);
            }

        } catch (EntidadeNaoLocalizada e) {
            throw new RuntimeException("Falha ao vincular: cuidador ou paciente não encontrado", e);
        }
    }

    @Override
    public void desvincular(String cpfCuidador, String cpfPaciente) {
        try {
            Cuidador cuidador = cuidadorRepository.buscarPorCpf(cpfCuidador);
            Paciente paciente = pacienteRepository.buscarPorCpf(cpfPaciente);

            if (cuidador.getPacientes() != null) {
                cuidador.getPacientes().removeIf(p -> p.getCpf().equals(paciente.getCpf()));
                paciente.setCuidador(null);
                cuidadorRepository.editar(cuidador);
            }

        } catch (EntidadeNaoLocalizada e) {
            throw new RuntimeException("Falha ao desvincular: cuidador ou paciente não encontrado", e);
        }
    }

    @Override
    public boolean isPacienteDoCuidador(String cpfCuidador, String cpfPaciente) {
        try {
            Cuidador cuidador = cuidadorRepository.buscarPorCpf(cpfCuidador);
            if (cuidador.getPacientes() == null) return false;
            return cuidador.getPacientes().stream()
                    .anyMatch(p -> p.getCpf().equals(cpfPaciente));
        } catch (EntidadeNaoLocalizada e) {
            return false;
        }
    }
}
