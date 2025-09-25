package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.model.Endereco;
import br.com.fiap.acompanha.domain.repository.EnderecoRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;

import java.sql.*;

public class JdbcEnderecoRepository implements EnderecoRepository {


    private final DatabaseConnection databaseConnection;

    public JdbcEnderecoRepository(DatabaseConnection databaseConnection){
        this.databaseConnection = databaseConnection;
    }


    @Override
    public long obterIdEndereco (Endereco endereco) {
        String sql = """
        INSERT INTO ENDERECO (CEP, RUA, NUMERO, COMPLEMENTO, BAIRRO, ESTADO, CIDADE)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getRua());
            stmt.setString(3, endereco.getNumero());
            stmt.setString(4, endereco.getComplemento());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getCidade());


            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("A inserção do endereço falhou, nenhuma linha foi afetada.");
            }

            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                if (chavesGeradas.next()) {

                    return chavesGeradas.getLong(1);
                } else {
                    throw new SQLException("A inserção do endereço falhou, nenhum ID foi retornado.");
                }
            }

        } catch (SQLException e) {

            throw new InfraestruturaException("Erro ao salvar endereço e obter ID.", e);
        }
    }

}
