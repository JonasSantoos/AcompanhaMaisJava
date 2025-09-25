package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Endereco;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.EnderecoRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcCuidadorRepository implements CuidadorRepository {

        private final DatabaseConnection databaseConnection;

        public JdbcCuidadorRepository(DatabaseConnection databaseConnection){
            this.databaseConnection = databaseConnection;
        }

        @Override
        public Cuidador adicionar(Cuidador cuidador) {


            Endereco enderecoParaSalvar = cuidador.getEndereco();
            long idDoEnderecoSalvo = JdbcEnderecoRepository();

            String sql = """
                    INSERT INTO CUIDADOR (NOME, CPF, DATANASCIMENTO, SEXO, TELEFONE, EMAIL, SENHA, ID_ENDERECO, VERSION)
                    VALUES (?,?,?,?,?,?,?,?,?)
                    """;

            try (Connection conn = this.databaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, cuidador.getNome());
                stmt.setString(2, cuidador.getCpf());
                stmt.setString(3, cuidador.getDataNascimento());
                stmt.setString(4, String.valueOf(cuidador.getSexo()));
                stmt.setString(5, cuidador.getTelefone());
                stmt.setString(6, cuidador.getEmail());
                stmt.setString(7, cuidador.getSenha());
                stmt.setLong(8, );
                stmt.setLong(9, cuidador.getVersao());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new InfraestruturaException("Erro ao salvar, nenhuma linha da banco foi afetada");
                }

                return cuidador;

            } catch (SQLException e) {
                throw new InfraestruturaException("Erro ao salvar cuidador", e);
            }
        }

    @Override
    public Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {

        String sql = """
                SELECT NOME, CPF, TELEFONE, EMAIL, VERSION,
                CEP, RUA, NUMERO, COMPLEMENTO, BAIRRO, ESTADO, CIDADE FROM CLIENTE WHERE CPF = ?
                """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String nome = resultSet.getString("NOME");
                String cpfFromDB = resultSet.getString("CPF");
                String telefone = resultSet.getString("TELEFONE");
                String email = resultSet.getString("EMAIL");
                Long versao = resultSet.getLong("VERSION");
                String cep = resultSet.getString("CEP");
                String rua = resultSet.getString("RUA");
                String numero = resultSet.getString("NUMERO");
                String complemento = resultSet.getString("COMPLEMENTO");
                String bairro = resultSet.getString("BAIRRO");
                String estado = resultSet.getString("ESTADO");
                String cidade = resultSet.getString("CIDADE");


                Endereco endereco = new Endereco(cep, rua, numero, complemento, bairro, estado, cidade);

                resultSet.close();

                return new Cuidador(nome, cpfFromDB, telefone, endereco, email, versao);
            }

        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar cuidador por cpf", e);
        }
        throw new EntidadeNaoLocalizada("Cuidador não encontrado");

    }

    @Override
    public Cuidador editar(Cuidador cuidador) {
        return null;
    }

    @Override
    public List<Cuidador> buscarTodos() {
        return List.of();
    }

    @Override
    public Cuidador excluirCuidador(String cpf, Long versao) {
        return null;
    }

}
