package br.com.fiap.acompanha.domain.repository;

import br.com.fiap.acompanha.domain.model.Paciente;

import java.util.List;

public interface PacienteRepository {

    Paciente buscarPorCpf(String cpf);
    List<Paciente> buscarTodos();

}
