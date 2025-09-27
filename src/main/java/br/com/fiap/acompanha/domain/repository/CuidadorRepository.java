package br.com.fiap.acompanha.domain.repository;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import java.util.List;

public interface CuidadorRepository {

    Cuidador adicionar(Cuidador cuidador);
    Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    Cuidador editar(Cuidador cuidador);

    List<Cuidador> buscarTodos();

    Cuidador excluirCuidador(String cpf, Long versao);
}
