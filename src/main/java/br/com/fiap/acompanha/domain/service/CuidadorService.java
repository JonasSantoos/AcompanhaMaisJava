package br.com.fiap.acompanha.domain.service;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;

import java.util.List;

public interface CuidadorService {

    Cuidador criar(Cuidador cuidador) throws EntidadeNaoLocalizada;
    Cuidador atualizar(Cuidador cuidador) throws EntidadeNaoLocalizada;
    void deletarCuidador(String cpf) throws EntidadeNaoLocalizada;
    Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    Cuidador vincularPaciente(String cpfCuidador, String cpfPaciente) throws EntidadeNaoLocalizada;
    List<Cuidador> listarTodos();
    Cuidador buscarPorEmail(String email) throws EntidadeNaoLocalizada;
}