package br.com.fiap.acompanha.application.service;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.domain.service.PacienteService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public List<Paciente> listarTodos() {
        return pacienteRepository.listarTodos();
    }

    @Override
    public Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        Paciente paciente = pacienteRepository.buscarPorCpf(cpf);
        if (paciente == null) {
            throw new EntidadeNaoLocalizada("Paciente não encontrado com o CPF: " + cpf);
        }
        return paciente;
    }

    @Override
    public Paciente excluirPaciente(String cpf) {
        return pacienteRepository.excluirPaciente(cpf);
    }

    @Override
    public Paciente atualizar(Paciente paciente) {
        return pacienteRepository.atualizar(paciente);
    }

    @Override
    public void vincularEndereco(Long idPaciente, Long idEndereco) {
        pacienteRepository.vincularEndereco(idPaciente, idEndereco);
    }

    @Override
    public Long criarEndereco(String rua, String numero, String complemento, String bairro,
                              String cidade, String estado, String cep) {
        return pacienteRepository.criarEndereco(rua, numero, complemento, bairro, cidade, estado, cep);
    }

    @Override
    public String construirEnderecoCompleto(ResultSet resultSet) throws SQLException {
        return pacienteRepository.construirEnderecoCompleto(resultSet);
    }
}