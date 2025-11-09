package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPacienteRepository implements PacienteRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcPacienteRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Paciente> listarTodos() {
        String sql = """
        SELECT
            P.ID_PACIENTE, P.NM_PACIENTE, P.CPF_PACIENTE, P.DT_NASCIMENTO_PACIENTE,
            P.SEXO_PACIENTE, P.TEL_PACIENTE, P.ESPECIALIDADE_ATENDIMENTO, P.VERSION,
            E.RUA, E.NUMERO, E.COMPLEMENTO, E.BAIRRO, E.CIDADE, E.ESTADO, E.CEP
        FROM
            ACPH_PACIENTE P
        LEFT JOIN
            ACPH_PACIENTE_ENDERECO PE ON P.ID_PACIENTE = PE.ACPH_PACIENTE_ID_PACIENTE
        LEFT JOIN
            ACPH_ENDERECO E ON PE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO
        ORDER BY P.ID_PACIENTE
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            List<Paciente> pacientes = new ArrayList<>();
            int count = 0;

            while (resultSet.next()) {
                count++;
                Long idPessoa = resultSet.getLong("ID_PACIENTE");
                String nome = resultSet.getString("NM_PACIENTE");
                String cpf = resultSet.getString("CPF_PACIENTE");
                Date dataNascimento = resultSet.getDate("DT_NASCIMENTO_PACIENTE");
                String sexoStr = resultSet.getString("SEXO_PACIENTE");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("TEL_PACIENTE");
                String especialidadeAtendimento = resultSet.getString("ESPECIALIDADE_ATENDIMENTO");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                Paciente paciente = new Paciente(
                        idPessoa, nome, cpf, dataNascimento, sexo, telefone,
                        endereco, especialidadeAtendimento, versao
                );

                pacientes.add(paciente);
            }

            return pacientes;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar pacientes", e);
        }
    }

    @Override
    public Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        String sql = """
        SELECT
            P.ID_PACIENTE, P.NM_PACIENTE, P.CPF_PACIENTE, P.DT_NASCIMENTO_PACIENTE,
            P.SEXO_PACIENTE, P.TEL_PACIENTE, P.ESPECIALIDADE_ATENDIMENTO, P.VERSION,
            E.RUA, E.NUMERO, E.COMPLEMENTO, E.BAIRRO, E.CIDADE, E.ESTADO, E.CEP
        FROM
            ACPH_PACIENTE P
        LEFT JOIN
            ACPH_PACIENTE_ENDERECO PE ON P.ID_PACIENTE = PE.ACPH_PACIENTE_ID_PACIENTE
        LEFT JOIN
            ACPH_ENDERECO E ON PE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO
        WHERE
            P.CPF_PACIENTE = ?
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("ID_PACIENTE");
                String nome = resultSet.getString("NM_PACIENTE");
                String cpfFromDB = resultSet.getString("CPF_PACIENTE");
                Date dataNascimento = resultSet.getDate("DT_NASCIMENTO_PACIENTE");
                String sexoStr = resultSet.getString("SEXO_PACIENTE");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("TEL_PACIENTE");
                String especialidadeAtendimento = resultSet.getString("ESPECIALIDADE_ATENDIMENTO");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                return new Paciente(
                        idPessoa, nome, cpfFromDB, dataNascimento, sexo, telefone,
                        endereco, especialidadeAtendimento, versao
                );
            }

            throw new EntidadeNaoLocalizada("Paciente não encontrado para CPF: " + cpf);

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar paciente pelo CPF", e);
        }
    }

    @Override
    public Paciente excluirPaciente(String cpf) {
        Paciente pacienteExistente;

        try {
            pacienteExistente = buscarPorCpf(cpf);
        } catch (EntidadeNaoLocalizada e) {
            throw new InfraestruturaException("Paciente não encontrado para exclusão.", e);
        }

        Connection conn = null;
        try {
            conn = this.databaseConnection.getConnection();
            conn.setAutoCommit(false);

            String desvincularEnderecoSql = "DELETE FROM ACPH_PACIENTE_ENDERECO WHERE ACPH_PACIENTE_ID_PACIENTE = ?";
            try (PreparedStatement stmt = conn.prepareStatement(desvincularEnderecoSql)) {
                stmt.setLong(1, pacienteExistente.getIdPessoa());
                stmt.executeUpdate();
            }

            String excluirPacienteSql = "DELETE FROM ACPH_PACIENTE WHERE CPF_PACIENTE = ?";
            try (PreparedStatement stmt = conn.prepareStatement(excluirPacienteSql)) {
                stmt.setString(1, cpf);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new InfraestruturaException("Erro ao excluir: nenhuma linha foi afetada.");
                }
            }

            conn.commit();
            return pacienteExistente;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            throw new InfraestruturaException("Erro ao excluir paciente", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Paciente atualizar(Paciente paciente) {
        String sql = """
    UPDATE ACPH_PACIENTE
    SET nm_paciente = ?, sexo_paciente = ?, dt_nascimento_paciente = ?,
        tel_paciente = ?, especialidade_atendimento = ?,
        ACPH_CUIDADOR_ID_CUIDADOR = ?, VERSION = ?
    WHERE cpf_paciente = ? AND VERSION = ?
    """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, String.valueOf(paciente.getSexo()));

            if (paciente.getDataNascimento() != null) {
                java.util.Date utilDate = paciente.getDataNascimento();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                stmt.setDate(3, sqlDate);
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getEspecialidadeAtendimento());

            if (paciente.getCuidador() != null) {
                stmt.setLong(6, paciente.getCuidador().getIdPessoa());
            } else {
                stmt.setNull(6, java.sql.Types.NUMERIC);
            }

            stmt.setLong(7, paciente.getVersao() + 1);
            stmt.setString(8, paciente.getCpf());
            stmt.setLong(9, paciente.getVersao());

            System.out.println("Executando UPDATE para paciente CPF: " + paciente.getCpf());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new EntidadeNaoLocalizada("Paciente não encontrado ou versão incorreta");
            }

            paciente.setVersao(paciente.getVersao() + 1);
            return paciente;

        } catch (SQLException e) {
            System.err.println("ERRO no UPDATE do paciente: " + e.getMessage());
            e.printStackTrace();
            throw new InfraestruturaException("Erro ao editar paciente", e);
        } catch (EntidadeNaoLocalizada e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void vincularEndereco(Long idPaciente, Long idEndereco) {
        String sql = "INSERT INTO ACPH_PACIENTE_ENDERECO (ACPH_PACIENTE_ID_PACIENTE, ACPH_ENDERECO_ID_ENDERECO) VALUES (?, ?)";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);
            stmt.setLong(2, idEndereco);

            System.out.println("Vinculando paciente " + idPaciente + " com endereço " + idEndereco);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao vincular endereço ao paciente");
            }

            System.out.println("Endereço " + idEndereco + " vinculado ao paciente " + idPaciente + " com sucesso!");

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao vincular endereço ao paciente", e);
        }
    }

    @Override
    public Long criarEndereco(String rua, String numero, String complemento, String bairro,
                              String cidade, String estado, String cep) {
        String sequenceSql = "SELECT SEQ_ENDERECO.NEXTVAL FROM DUAL";
        Long nextId = null;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement seqStmt = conn.prepareStatement(sequenceSql);
             ResultSet rs = seqStmt.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getLong(1);
                System.out.println("Próximo ID da sequence de endereço: " + nextId);
            }
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao obter sequence do endereço", e);
        }

        String sql = """
        INSERT INTO ACPH_ENDERECO
        (id_endereco, rua, numero, complemento, bairro, cidade, estado, cep)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, nextId);
            stmt.setString(2, rua);
            stmt.setString(3, numero);
            stmt.setString(4, complemento);
            stmt.setString(5, bairro);
            stmt.setString(6, cidade);
            stmt.setString(7, estado);
            stmt.setString(8, cep);

            System.out.println("Executando INSERT do endereço com ID: " + nextId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao criar endereço, nenhuma linha foi inserida.");
            }

            System.out.println("Endereço criado com sucesso! ID: " + nextId);
            return nextId;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao criar endereço", e);
        }
    }

    @Override
    public String construirEnderecoCompleto(ResultSet resultSet) throws SQLException {
        StringBuilder endereco = new StringBuilder();

        String rua = resultSet.getString("rua");
        String numero = resultSet.getString("numero");
        String complemento = resultSet.getString("complemento");
        String bairro = resultSet.getString("bairro");
        String cidade = resultSet.getString("cidade");
        String estado = resultSet.getString("estado");
        String cep = resultSet.getString("cep");

        if (rua != null) {
            endereco.append(rua);
            if (numero != null) {
                endereco.append(", ").append(numero);
            }
            if (complemento != null && !complemento.trim().isEmpty()) {
                endereco.append(" - ").append(complemento);
            }
        }

        if (bairro != null) {
            if (endereco.length() > 0) endereco.append(" - ");
            endereco.append(bairro);
        }

        if (cidade != null) {
            if (endereco.length() > 0) endereco.append(", ");
            endereco.append(cidade);
        }

        if (estado != null) {
            if (endereco.length() > 0) endereco.append("/");
            endereco.append(estado);
        }

        if (cep != null) {
            if (endereco.length() > 0) endereco.append(" - CEP: ");
            endereco.append(cep);
        }

        return endereco.length() > 0 ? endereco.toString() : null;
    }

    private Long obterProximoId(Connection conn, String sequenceName) throws SQLException {
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new InfraestruturaException("Erro ao obter próximo ID da sequence: " + sequenceName);
        }
    }

    private void inserirPaciente(Connection conn, Long idPaciente, Paciente paciente) throws SQLException {
        String sql = """
        INSERT INTO ACPH_PACIENTE
        (id_paciente, nm_paciente, sexo_paciente, cpf_paciente, dt_nascimento_paciente,
         tel_paciente, especialidade_atendimento, VERSION)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPaciente);
            stmt.setString(2, paciente.getNome());
            stmt.setString(3, String.valueOf(paciente.getSexo()));
            stmt.setString(4, paciente.getCpf());

            if (paciente.getDataNascimento() != null) {
                java.util.Date utilDate = paciente.getDataNascimento();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                stmt.setDate(5, sqlDate);
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, paciente.getTelefone());
            stmt.setString(7, paciente.getEspecialidadeAtendimento());
            stmt.setLong(8, paciente.getVersao() != null ? paciente.getVersao() : 1L);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao criar paciente, nenhuma linha foi inserida.");
            }

            System.out.println("Paciente inserido com sucesso: " + paciente.getNome());
        }
    }

    private Long criarEnderecoTransacional(Connection conn, String rua, String numero, String complemento,
                                           String bairro, String cidade, String estado, String cep) throws SQLException {
        Long idEndereco = obterProximoId(conn, "SEQ_ENDERECO");

        String sql = """
        INSERT INTO ACPH_ENDERECO
        (id_endereco, rua, numero, complemento, bairro, cidade, estado, cep)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idEndereco);
            stmt.setString(2, rua);
            stmt.setString(3, numero);
            stmt.setString(4, complemento);
            stmt.setString(5, bairro);
            stmt.setString(6, cidade);
            stmt.setString(7, estado);
            stmt.setString(8, cep);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao criar endereço");
            }

            System.out.println("Endereço criado com sucesso! ID: " + idEndereco);
            return idEndereco;
        }
    }

    private void vincularEnderecoTransacional(Connection conn, Long idPaciente, Long idEndereco) throws SQLException {
        String sql = "INSERT INTO ACPH_PACIENTE_ENDERECO (ACPH_PACIENTE_ID_PACIENTE, ACPH_ENDERECO_ID_ENDERECO) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPaciente);
            stmt.setLong(2, idEndereco);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao vincular endereço ao paciente");
            }

            System.out.println("Endereço " + idEndereco + " vinculado ao paciente " + idPaciente);
        }
    }

    private String[] parseEndereco(String enderecoCompleto) {
        String rua = enderecoCompleto;
        String numero = "S/N";
        String complemento = "";
        String bairro = "Centro";
        String cidade = "São Paulo";
        String estado = "SP";
        String cep = "00000-000";

        if (enderecoCompleto.contains(",")) {
            String[] parts = enderecoCompleto.split(",");
            rua = parts[0].trim();
            if (parts.length > 1) {
                numero = parts[1].trim();
            }
        }

        return new String[]{rua, numero, complemento, bairro, cidade, estado, cep};
    }
}