package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;

import java.util.List;

public class JdbcPacienteRepository implements PacienteRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcPacienteRepository(DatabaseConnection databaseConnection){
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Paciente buscarPorCpf(String cpf) {
        return null;
    }

    @Override
    public Paciente excluirPaciente(String cpf, Long versao){
        return null;
    }

    @Override
    public List<Paciente> buscarTodos(){
        return null;
    }

}
