package br.com.fiap.acompanha.infrastructure.persistence;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcCuidadorRepository implements CuidadorRepository{

    private final DatabaseConnection databaseConnection;

    public JdbcCuidadorRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Cuidador adicionar(Cuidador cuidador) {

        String sql = """
                INSERT INTO ACPH_CUIDADOR
                (id_cuidador, nm_cuidador, sexo_cuidador, cpf_cuidador, dt_nascimento_cuidador,
                 tel_cuidador, email_cuidador, senha_cuidador,VERSION)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cuidador.getIdPessoa());
            stmt.setString(2, cuidador.getNome());
            stmt.setString(3, String.valueOf(cuidador.getSexo()));
            stmt.setString(4, cuidador.getCpf());
            stmt.setString(5, cuidador.getDataNascimento());
            stmt.setString(6, cuidador.getTelefone());
            stmt.setString(7, cuidador.getEmail());
            stmt.setString(8, cuidador.getSenha());
            stmt.setLong(9, cuidador.getVersao());

            return cuidador;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao salvar cuidador", e);
        }
    }

    @Override
    public Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {

        String sql = """
                SELECT id_cuidador, nm_cuidador, cpf_cuidador, tel_cuidador,
                       email_cuidador, id_endereco ,VERSION  FROM CLIENTE WHERE cpf_cuidador = ?
                """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("id_cuidador");
                String nome = resultSet.getString("nm_cuidador");
                String cpfFromDB = resultSet.getString("cpf_cuidador");
                String dataNascimento = resultSet.getString("dt_nascimento");
                char sexo = resultSet.getString("sexo_cuidador").charAt(0);
                String telefone = resultSet.getString("tel_cuidador");
                String email = resultSet.getString("email_cuidador");
                String senha = resultSet.getString("senha_cuidador");
                String endereco = resultSet.getString("id_endereco");
                Long versao = resultSet.getLong("VERSION");

                resultSet.close();

                return new Cuidador(
                        idPessoa, nome, cpfFromDB, dataNascimento, sexo, telefone,
                        endereco , email, senha, versao);
            }

        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar cuidador por cpf", e);
        }
        throw new EntidadeNaoLocalizada("Cuidador não encontrado");
    }

    @Override
    public Cuidador editar(Cuidador cuidador) {

        String sql = """
        UPDATE ACPH_CUIDADOR
        SET nm_cuidador = ?, sexo_cuidador = ?, dt_nascimento_cuidador = ?,
            tel_cuidador = ?, email_cuidador = ?, senha_cuidador = ?, VERSION = ?
        WHERE cpf_cuidador = ? AND VERSION = ?
        """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cuidador.getNome());
            stmt.setString(2, String.valueOf(cuidador.getSexo()));
            stmt.setDate(3, java.sql.Date.valueOf(cuidador.getDataNascimento()));
            stmt.setString(4, cuidador.getTelefone());
            stmt.setString(5, cuidador.getEmail());
            stmt.setString(6, cuidador.getSenha());
            stmt.setLong(7, cuidador.getVersao() + 1);
            stmt.setString(8, cuidador.getCpf());
            stmt.setLong(9, cuidador.getVersao());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new EntidadeNaoLocalizada("Cuidador não encontrado ou versão incorreta");
            }

            cuidador.setVersao(cuidador.getVersao() + 1);
            return cuidador;

        } catch (SQLException | EntidadeNaoLocalizada e) {
            throw new InfraestruturaException("Erro ao editar cuidador", e);
        }
    }


    @Override
    public List<Cuidador> buscarTodos() {

        String sql = "SELECT ID_CUIDADOR, NM_CUIDADOR, SEXO_CUIDADOR, CPF_CUIDADOR, " +
                "DT_NASCIMENTO_CUIDADOR, TEL_CUIDADOR, EMAIL_CUIDADOR, SENHA_CUIDADOR, VERSION " +
                "FROM ACPH_CUIDADOR";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Cuidador> cuidadores = new java.util.ArrayList<>();

            while (rs.next()) {
                Long idPessoa = rs.getLong("id_cuidador");
                String nome = rs.getString("nm_cuidador");
                String cpf = rs.getString("cpf_cuidador");
                String dataNascimento = rs.getString("dt_nascimento_cuidador");
                char sexo = rs.getString("sexo_cuidador").charAt(0);
                String telefone = rs.getString("tel_cuidador");
                String email = rs.getString("email_cuidador");
                String senha = rs.getString("senha_cuidador");
                Long versao = rs.getLong("VERSION");

                cuidadores.add(new Cuidador(idPessoa, nome, cpf, dataNascimento, sexo, telefone,
                        email, senha, versao));
            }

            return cuidadores;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar todos os cuidadores", e);
        }
    }

    @Override
    public Cuidador excluirCuidador(String cpf, Long versao) {

        String sql = "DELETE FROM ACPH_CUIDADOR WHERE cpf_cuidador = ? AND VERSION = ?";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setLong(2, versao);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao excluir, nenhuma linha do banco foi afetada");
            }

            return null;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao excluir cuidador", e);
        }

    }
}