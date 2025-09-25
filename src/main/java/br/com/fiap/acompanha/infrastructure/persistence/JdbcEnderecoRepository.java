package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.model.Endereco;
import br.com.fiap.acompanha.domain.repository.EnderecoRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;
import br.com.fiap.acompanha.infrastructure.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcEnderecoRepository implements EnderecoRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcEnderecoRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public long obterIdEndereco(Endereco endereco) {
        String sql = """
                INSERT INTO ENDERECO (CEP, RUA, NUMERO, COMPLEMENTO, BAIRRO, ESTADO, CIDADE)
                VALUES (?,?,?,?,?,?,?)
                """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getRua());
            stmt.setString(3, endereco.getNumero());
            stmt.setString(4, endereco.getComplemento());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getCidade());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            throw new InfraestruturaException("Não foi possível recuperar o ID do endereço");

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao salvar endereço", e);
        }
    }
}
