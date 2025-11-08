package br.com.fiap.acompanha.application.service;

import br.com.fiap.acompanha.application.exception.CuidadorUnsupportedOperation;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.domain.service.CuidadorService;

import java.util.ArrayList;
import java.util.List;

public class CuidadorServiceImpl implements CuidadorService {

    private final CuidadorRepository cuidadorRepository;
    private final PacienteRepository pacienteRepository;

    public CuidadorServiceImpl(CuidadorRepository cuidadorRepository, PacienteRepository pacienteRepository) {
        this.cuidadorRepository = cuidadorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Cuidador criar(Cuidador cuidador) throws EntidadeNaoLocalizada {
        try {

            cuidadorRepository.buscarPorCpf(cuidador.getCpf());
            throw new IllegalArgumentException("Já existe um cuidador cadastrado com o CPF: " + cuidador.getCpf());
        } catch (EntidadeNaoLocalizada e) {
            if (cuidadorRepository.emailExistente(cuidador.getEmail())) {
                throw new IllegalArgumentException("Já existe um cuidador cadastrado com o email: " + cuidador.getEmail());
            }

            return cuidadorRepository.criar(cuidador);
        }
    }

    @Override
    public Cuidador atualizar(Cuidador cuidador) throws EntidadeNaoLocalizada {
        Cuidador cuidadorExistente = cuidadorRepository.buscarPorCpf(cuidador.getCpf());
        if (cuidadorExistente == null) {
            throw new CuidadorUnsupportedOperation("Cuidador não encontrado para atualização.");
        }

        if (!cuidadorExistente.getEmail().equals(cuidador.getEmail()) &&
                cuidadorRepository.emailExistente(cuidador.getEmail())) {
            throw new IllegalArgumentException("Já existe um cuidador cadastrado com o email: " + cuidador.getEmail());
        }

        cuidadorExistente.setNome(cuidador.getNome());
        cuidadorExistente.setEmail(cuidador.getEmail());
        cuidadorExistente.setTelefone(cuidador.getTelefone());
        cuidadorExistente.setDataNascimento(cuidador.getDataNascimento());
        cuidadorExistente.setSexo(cuidador.getSexo());
        cuidadorExistente.setSenha(cuidador.getSenha());

        return cuidadorRepository.editar(cuidadorExistente);
    }

    @Override
    public void deletarCuidador(String cpf) throws EntidadeNaoLocalizada {
        Cuidador cuidadorExistente = cuidadorRepository.buscarPorCpf(cpf);
        if (cuidadorExistente == null) {
            throw new EntidadeNaoLocalizada("Cuidador não encontrado com o CPF: " + cpf);
        }
        cuidadorRepository.deletarCuidador(cpf);
    }



    @Override
    public Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        Cuidador cuidador = cuidadorRepository.buscarPorCpf(cpf);
        if (cuidador == null) {
            throw new EntidadeNaoLocalizada("Cuidador não encontrado com o CPF: " + cpf);
        }
        return cuidador;
    }

    @Override
    public Cuidador vincularPaciente(String cpfCuidador, String cpfPaciente) throws EntidadeNaoLocalizada {
        Cuidador cuidador = cuidadorRepository.buscarPorCpf(cpfCuidador);
        if (cuidador == null) {
            throw new EntidadeNaoLocalizada("Cuidador não encontrado com o CPF: " + cpfCuidador);
        }

        Paciente paciente = pacienteRepository.buscarPorCpf(cpfPaciente);
        if (paciente == null) {
            throw new EntidadeNaoLocalizada("Paciente não encontrado com o CPF: " + cpfPaciente);
        }

        paciente.setCuidador(cuidador);
        pacienteRepository.atualizar(paciente);

        Cuidador cuidadorAtualizado = cuidadorRepository.buscarPorCpf(cpfCuidador);

        return cuidadorAtualizado;
    }

    @Override
    public List<Cuidador> listarTodos() {
        return cuidadorRepository.listarTodos();
    }

    @Override
    public Cuidador buscarPorEmail(String email) throws EntidadeNaoLocalizada {
        return cuidadorRepository.buscarPorEmail(email);
    }

}