package br.com.fiap.acompanha.domain.repository;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import java.util.List;

public interface PacienteRepository {

    Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    Paciente excluirPaciente(String cpf, Long versao) throws EntidadeNaoLocalizada;
    List<Paciente> buscarTodos();

}
