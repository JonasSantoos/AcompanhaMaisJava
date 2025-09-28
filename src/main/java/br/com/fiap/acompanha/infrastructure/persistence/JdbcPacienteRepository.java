package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPacienteRepository implements PacienteRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcPacienteRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        String sql = """
            
                SELECT
                P.ID_PACIENTE, P.NM_PACIENTE, P.CPF_PACIENTE, P.DT_NASCIMENTO_PACIENTE,
                P.SEXO_PACIENTE, P.TEL_PACIENTE, P.ESPECIALIDADE_ATENDIMENTO, P.VERSION,
                E.RUA || ', ' || E.NUMERO || ' - ' || E.BAIRRO || ', ' || E.CIDADE || ' / ' || E.ESTADO AS ENDERECO_COMPLETO
            FROM
                ACPH_PACIENTE P
            LEFT JOIN
                ACPH_PACIENTE_ENDERECO PE ON P.ID_PACIENTE = PE.ACPH_PACIENTE_ID_PACIENTE
            LEFT JOIN
                ACPH_ENDERECO E ON PE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO
            WHERE
                P.CPF_PACIENTE = ?
            FETCH FIRST 1 ROW ONLY
            """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("id_paciente");
                String nome = resultSet.getString("nm_paciente");
                String cpfFromDB = resultSet.getString("cpf_paciente");
                String dataNascimento = resultSet.getString("dt_nascimento_paciente");
                char sexo = resultSet.getString("sexo_paciente").charAt(0);
                String telefone = resultSet.getString("tel_paciente");
                // Usando o alias da coluna de endereço que criamos
                String endereco = resultSet.getString("ENDERECO_COMPLETO");
                String especialidadeAtendimento = resultSet.getString("especialidade_atendimento");
                Long versao = resultSet.getLong("VERSION");

                resultSet.close();

                return new Paciente(
                        idPessoa, nome, cpfFromDB, dataNascimento, sexo, telefone,
                        endereco, especialidadeAtendimento, versao);
            }

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar paciente por cpf", e);
        }
        throw new EntidadeNaoLocalizada("Paciente não encontrado para o CPF: " + cpf);
    }


    @Override
    public Paciente excluirPaciente(String cpf, Long versao) throws EntidadeNaoLocalizada {
        Paciente pacienteExistente = null;
        try {
            pacienteExistente = buscarPorCpf(cpf);
        } catch (EntidadeNaoLocalizada e) {
            throw new InfraestruturaException("Paciente não encontrado para exclusão.", e);
        }

        String sql = "DELETE FROM ACPH_PACIENTE WHERE cpf_paciente = ? AND VERSION = ?";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setLong(2, versao);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao excluir, nenhuma linha do banco foi afetada ou versão incorreta");
            }

            return pacienteExistente;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao excluir paciente", e);
        }
    }

    @Override
    public List<Paciente> buscarTodos() {
        String sql = """
            SELECT
                P.ID_PACIENTE, P.NM_PACIENTE, P.CPF_PACIENTE, P.DT_NASCIMENTO_PACIENTE,
                P.SEXO_PACIENTE, P.TEL_PACIENTE, P.ESPECIALIDADE_ATENDIMENTO, P.VERSION,
                E.RUA || ', ' || E.NUMERO || ' - ' || E.BAIRRO || ', ' || E.CIDADE || ' / ' || E.ESTADO AS ENDERECO_COMPLETO
            FROM
                ACPH_PACIENTE P
            LEFT JOIN
                ACPH_PACIENTE_ENDERECO PE ON P.ID_PACIENTE = PE.ACPH_PACIENTE_ID_PACIENTE
            LEFT JOIN
                ACPH_ENDERECO E ON PE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO
            ORDER BY P.NM_PACIENTE
            """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Paciente> pacientes = new ArrayList<>();

            while (rs.next()) {
                Long idPessoa = rs.getLong("id_paciente");
                String nome = rs.getString("nm_paciente");
                String cpf = rs.getString("cpf_paciente");
                String dataNascimento = rs.getString("dt_nascimento_paciente");
                char sexo = rs.getString("sexo_paciente").charAt(0);
                String telefone = rs.getString("tel_paciente");
                String endereco = rs.getString("ENDERECO_COMPLETO");
                String especialidadeAtendimento = rs.getString("especialidade_atendimento");
                Long versao = rs.getLong("VERSION");

                pacientes.add(new Paciente(idPessoa, nome, cpf, dataNascimento, sexo, telefone,
                        endereco, especialidadeAtendimento, versao));
            }

            return pacientes;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar todos os pacientes", e);
        }
    }
}