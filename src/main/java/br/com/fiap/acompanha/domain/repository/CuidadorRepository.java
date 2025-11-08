package br.com.fiap.acompanha.domain.repository;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CuidadorRepository {

    Cuidador criar(Cuidador cuidador); // Já cria com endereço internamente
    Cuidador buscarPorId(Long id) throws EntidadeNaoLocalizada;
    Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    Cuidador editar(Cuidador cuidador);
    void deletarCuidador(String cpf);
    Cuidador buscarPorEmail(String email) throws EntidadeNaoLocalizada;
    boolean emailExistente(String email);
    List<Cuidador> listarTodos();
    void vincularEndereco(Long idCuidador, Long idEndereco);

    Long criarEndereco(String rua, String numero, String complemento, String bairro,
                              String cidade, String estado, String cep);

    public String construirEnderecoCompleto(ResultSet resultSet) throws SQLException;


}