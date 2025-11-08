package br.com.fiap.acompanha.domain.service;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface PacienteService {

    List<Paciente> listarTodos();
    Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    Paciente excluirPaciente(String cpf);
    Paciente atualizar(Paciente paciente);
    void vincularEndereco(Long idPaciente, Long idEndereco);
    Long criarEndereco(String rua, String numero, String complemento, String bairro,
                       String cidade, String estado, String cep);
    String construirEnderecoCompleto(ResultSet resultSet) throws SQLException;
}