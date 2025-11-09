package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcCuidadorRepository implements CuidadorRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcCuidadorRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Cuidador buscarPorId(Long id) throws EntidadeNaoLocalizada {
        String sql = """
    SELECT 
        c.id_cuidador, c.nm_cuidador, c.cpf_cuidador, c.dt_nascimento_cuidador,
        c.sexo_cuidador, c.tel_cuidador, c.email_cuidador, c.senha_cuidador,
        c.VERSION,
        e.id_endereco, e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado, e.cep
    FROM ACPH_CUIDADOR c
    LEFT JOIN ACPH_CUIDADOR_ENDERECO ce ON c.id_cuidador = ce.ACPH_CUIDADOR_ID_CUIDADOR
    LEFT JOIN ACPH_ENDERECO e ON ce.ACPH_ENDERECO_ID_ENDERECO = e.id_endereco
    WHERE c.id_cuidador = ?
    """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("id_cuidador");
                String nome = resultSet.getString("nm_cuidador");
                String cpfFromDB = resultSet.getString("cpf_cuidador");
                java.sql.Date dataNascimento = resultSet.getDate("dt_nascimento_cuidador");
                String sexoStr = resultSet.getString("sexo_cuidador");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("tel_cuidador");
                String email = resultSet.getString("email_cuidador");
                String senha = resultSet.getString("senha_cuidador");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                LocalDate dataNascimentoLocalDate = null;
                if (dataNascimento != null) {
                    dataNascimentoLocalDate = dataNascimento.toLocalDate();
                }

                return new Cuidador(
                        idPessoa, nome, cpfFromDB, dataNascimentoLocalDate, sexo,
                        telefone, endereco, email, senha, versao
                );
            }

            throw new EntidadeNaoLocalizada("Cuidador não encontrado para ID: " + id);

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar cuidador por ID", e);
        }
    }

    @Override
    public Cuidador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        String sql = """
SELECT
    c.id_cuidador, c.nm_cuidador, c.cpf_cuidador, c.dt_nascimento_cuidador,
    c.sexo_cuidador, c.tel_cuidador, c.email_cuidador, c.senha_cuidador,
    c.VERSION,
    e.id_endereco, e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado, e.cep
FROM ACPH_CUIDADOR c
LEFT JOIN ACPH_CUIDADOR_ENDERECO ce ON c.id_cuidador = ce.ACPH_CUIDADOR_ID_CUIDADOR
LEFT JOIN ACPH_ENDERECO e ON ce.ACPH_ENDERECO_ID_ENDERECO = e.id_endereco
WHERE c.cpf_cuidador = ?
""";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("id_cuidador");
                String nome = resultSet.getString("nm_cuidador");
                String cpfFromDB = resultSet.getString("cpf_cuidador");
                java.sql.Date dataNascimento = resultSet.getDate("dt_nascimento_cuidador");
                String sexoStr = resultSet.getString("sexo_cuidador");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("tel_cuidador");
                String email = resultSet.getString("email_cuidador");
                String senha = resultSet.getString("senha_cuidador");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                LocalDate dataNascimentoLocalDate = null;
                if (dataNascimento != null) {
                    dataNascimentoLocalDate = dataNascimento.toLocalDate();
                }

                Cuidador cuidador = new Cuidador(
                        idPessoa, nome, cpfFromDB, dataNascimentoLocalDate, sexo,
                        telefone, endereco, email, senha, versao
                );

                List<Paciente> pacientes = buscarPacientesPorCuidador(idPessoa);
                cuidador.setPacientes(pacientes);

                return cuidador;
            }

            throw new EntidadeNaoLocalizada("Cuidador não encontrado com CPF: " + cpf);

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar cuidador por CPF", e);
        }
    }

    private List<Paciente> buscarPacientesPorCuidador(Long idCuidador) {
        String sql = """
SELECT
    P.ID_PACIENTE, P.NM_PACIENTE, P.CPF_PACIENTE, P.DT_NASCIMENTO_PACIENTE,
    P.SEXO_PACIENTE, P.TEL_PACIENTE, P.ESPECIALIDADE_ATENDIMENTO, P.VERSION,
    E.RUA, E.NUMERO, E.COMPLEMENTO, E.BAIRRO, E.CIDADE, E.ESTADO, E.CEP
FROM ACPH_PACIENTE P
LEFT JOIN ACPH_PACIENTE_ENDERECO PE ON P.ID_PACIENTE = PE.ACPH_PACIENTE_ID_PACIENTE
LEFT JOIN ACPH_ENDERECO E ON PE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO
WHERE P.ACPH_CUIDADOR_ID_CUIDADOR = ?
""";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCuidador);
            ResultSet resultSet = stmt.executeQuery();

            List<Paciente> pacientes = new ArrayList<>();

            while (resultSet.next()) {
                Long idPessoa = resultSet.getLong("ID_PACIENTE");
                String nome = resultSet.getString("NM_PACIENTE");
                String cpf = resultSet.getString("CPF_PACIENTE");
                java.sql.Date dataNascimento = resultSet.getDate("DT_NASCIMENTO_PACIENTE");
                String sexoStr = resultSet.getString("SEXO_PACIENTE");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("TEL_PACIENTE");
                String especialidadeAtendimento = resultSet.getString("ESPECIALIDADE_ATENDIMENTO");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                // CONVERTER java.sql.Date para LocalDate
                LocalDate dataNascimentoLocalDate = null;
                if (dataNascimento != null) {
                    dataNascimentoLocalDate = dataNascimento.toLocalDate();
                }

                Paciente paciente = new Paciente(
                        idPessoa, nome, cpf, dataNascimentoLocalDate, sexo, telefone,
                        endereco, especialidadeAtendimento, versao
                );

                pacientes.add(paciente);
            }

            return pacientes;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar pacientes do cuidador: " + e.getMessage());
            return new ArrayList<>();
        }
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

            if (cuidador.getDataNascimento() != null) {
                cuidador.setDataNascimento(cuidador.getDataNascimento());
                java.sql.Date sqlDate = java.sql.Date.valueOf(cuidador.getDataNascimento());
                stmt.setDate(3, sqlDate);
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, cuidador.getTelefone());
            stmt.setString(5, cuidador.getEmail());
            stmt.setString(6, cuidador.getSenha());
            stmt.setLong(7, cuidador.getVersao() + 1);
            stmt.setString(8, cuidador.getCpf());
            stmt.setLong(9, cuidador.getVersao());

            System.out.println("=== EXECUTANDO UPDATE ===");
            System.out.println("CPF: " + cuidador.getCpf());
            System.out.println("Nova senha no objeto: " + cuidador.getSenha());
            System.out.println("Versão atual: " + cuidador.getVersao());
            System.out.println("Nova versão: " + (cuidador.getVersao() + 1));

            int affectedRows = stmt.executeUpdate();
            System.out.println("Linhas afetadas: " + affectedRows);

            if (affectedRows == 0) {
                throw new EntidadeNaoLocalizada("Cuidador não encontrado ou versão incorreta");
            }

            cuidador.setVersao(cuidador.getVersao() + 1);
            System.out.println("Update concluído com sucesso!");
            return cuidador;

        } catch (SQLException e) {
            System.err.println("ERRO no UPDATE: " + e.getMessage());
            e.printStackTrace();
            throw new InfraestruturaException("Erro ao editar cuidador", e);
        } catch (EntidadeNaoLocalizada e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletarCuidador(String cpf) {
        Connection conn = null;
        try {
            conn = this.databaseConnection.getConnection();
            conn.setAutoCommit(false);

            String buscarIdSql = "SELECT ID_CUIDADOR FROM ACPH_CUIDADOR WHERE CPF_CUIDADOR = ?";
            Long idCuidador = null;

            try (PreparedStatement stmtBuscar = conn.prepareStatement(buscarIdSql)) {
                stmtBuscar.setString(1, cpf);
                ResultSet rs = stmtBuscar.executeQuery();
                if (rs.next()) {
                    idCuidador = rs.getLong("ID_CUIDADOR");
                }
            }

            if (idCuidador == null) {
                throw new InfraestruturaException("Cuidador não encontrado com CPF: " + cpf);
            }

            System.out.println("Desvinculando relações do cuidador ID: " + idCuidador);

            try {
                String excluirConsultasSql = "DELETE FROM ACPH_CONSULTA WHERE ACPH_CUIDADOR_ID_CUIDADOR = ?";
                try (PreparedStatement stmt = conn.prepareStatement(excluirConsultasSql)) {
                    stmt.setLong(1, idCuidador);
                    int rows = stmt.executeUpdate();
                    System.out.println("Consultas excluídas: " + rows);
                }
            } catch (SQLException e) {
                System.out.println("Nenhuma consulta vinculada ou erro ao excluir: " + e.getMessage());
            }

            try {
                String desvincularPacientesSql = "UPDATE ACPH_PACIENTE SET ACPH_CUIDADOR_ID_CUIDADOR = NULL WHERE ACPH_CUIDADOR_ID_CUIDADOR = ?";
                try (PreparedStatement stmt = conn.prepareStatement(desvincularPacientesSql)) {
                    stmt.setLong(1, idCuidador);
                    int rows = stmt.executeUpdate();
                    System.out.println("Pacientes desvinculados: " + rows);
                }
            } catch (SQLException e) {
                System.out.println("Nenhum paciente vinculado ou erro ao desvincular: " + e.getMessage());
            }

            try {
                String desvincularEnderecosSql = "DELETE FROM ACPH_CUIDADOR_ENDERECO WHERE ACPH_CUIDADOR_ID_CUIDADOR = ?";
                try (PreparedStatement stmt = conn.prepareStatement(desvincularEnderecosSql)) {
                    stmt.setLong(1, idCuidador);
                    int rows = stmt.executeUpdate();
                    System.out.println("Endereços desvinculados: " + rows);
                }
            } catch (SQLException e) {
                System.out.println("Nenhum endereço vinculado ou erro ao desvincular: " + e.getMessage());
            }

            String excluirCuidadorSql = "DELETE FROM ACPH_CUIDADOR WHERE CPF_CUIDADOR = ?";
            try (PreparedStatement stmtExcluir = conn.prepareStatement(excluirCuidadorSql)) {
                stmtExcluir.setString(1, cpf);
                int affectedRows = stmtExcluir.executeUpdate();

                if (affectedRows == 0) {
                    throw new InfraestruturaException("Erro ao excluir cuidador: nenhuma linha foi afetada.");
                }
                System.out.println("Cuidador excluído com sucesso!");
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("ROLLBACK executado devido a erro: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            throw new InfraestruturaException("Erro ao excluir cuidador", e);
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
    public Cuidador buscarPorEmail(String email) throws EntidadeNaoLocalizada {
        String sql = """
SELECT
    c.id_cuidador, c.nm_cuidador, c.cpf_cuidador, c.dt_nascimento_cuidador,
    c.sexo_cuidador, c.tel_cuidador, c.email_cuidador, c.senha_cuidador,
    c.VERSION,
    e.id_endereco, e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado, e.cep
FROM ACPH_CUIDADOR c
LEFT JOIN ACPH_CUIDADOR_ENDERECO ce ON c.id_cuidador = ce.ACPH_CUIDADOR_ID_CUIDADOR
LEFT JOIN ACPH_ENDERECO e ON ce.ACPH_ENDERECO_ID_ENDERECO = e.id_endereco
WHERE c.email_cuidador = ?
""";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Long idPessoa = resultSet.getLong("id_cuidador");
                String nome = resultSet.getString("nm_cuidador");
                String cpf = resultSet.getString("cpf_cuidador");
                java.sql.Date dataNascimento = resultSet.getDate("dt_nascimento_cuidador");
                String sexoStr = resultSet.getString("sexo_cuidador");
                char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ';
                String telefone = resultSet.getString("tel_cuidador");
                String emailFromDB = resultSet.getString("email_cuidador");
                String senha = resultSet.getString("senha_cuidador");
                Long versao = resultSet.getLong("VERSION");

                String endereco = construirEnderecoCompleto(resultSet);

                // CONVERTER java.sql.Date para LocalDate
                LocalDate dataNascimentoLocalDate = null;
                if (dataNascimento != null) {
                    dataNascimentoLocalDate = dataNascimento.toLocalDate();
                }

                Cuidador cuidador = new Cuidador(
                        idPessoa, nome, cpf, dataNascimentoLocalDate, sexo,
                        telefone, endereco, emailFromDB, senha, versao
                );

                List<Paciente> pacientes = buscarPacientesPorCuidador(idPessoa);
                cuidador.setPacientes(pacientes);

                return cuidador;
            }

            throw new EntidadeNaoLocalizada("Cuidador não encontrado com e-mail: " + email);

        } catch (SQLException e) {
            System.err.println("ERRO ao buscar por email: " + e.getMessage());
            e.printStackTrace();
            throw new InfraestruturaException("Erro ao buscar cuidador por e-mail", e);
        }
    }

    @Override
    public boolean emailExistente(String email) {
        String sql = "SELECT COUNT(*) AS total FROM ACPH_CUIDADOR WHERE email_cuidador = ?";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao verificar existência de e-mail", e);
        }
    }

    @Override
    public List<Cuidador> listarTodos() {
        System.out.println("Repository: Executando listarTodos - TRAZENDO TODOS DADOS");

        String sql = """
        SELECT id_cuidador, nm_cuidador, cpf_cuidador, email_cuidador, tel_cuidador 
        FROM ACPH_CUIDADOR 
        ORDER BY id_cuidador
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            List<Cuidador> cuidadores = new ArrayList<>();
            int count = 0;

            while (resultSet.next()) {
                count++;
                Long id = resultSet.getLong("id_cuidador");
                String nome = resultSet.getString("nm_cuidador");
                String cpf = resultSet.getString("cpf_cuidador");
                String email = resultSet.getString("email_cuidador");
                String telefone = resultSet.getString("tel_cuidador");

                System.out.println("Cuidador " + count + ": ID=" + id + ", Nome=" + nome);

                Cuidador cuidador = new Cuidador();

                try {
                    java.lang.reflect.Field idField = cuidador.getClass().getSuperclass().getDeclaredField("idPessoa");
                    idField.setAccessible(true);
                    idField.set(cuidador, id);

                    java.lang.reflect.Field nomeField = cuidador.getClass().getSuperclass().getDeclaredField("nome");
                    nomeField.setAccessible(true);
                    nomeField.set(cuidador, nome);

                    java.lang.reflect.Field cpfField = cuidador.getClass().getSuperclass().getDeclaredField("cpf");
                    cpfField.setAccessible(true);
                    cpfField.set(cuidador, cpf);

                    java.lang.reflect.Field emailField = cuidador.getClass().getDeclaredField("email");
                    emailField.setAccessible(true);
                    emailField.set(cuidador, email);

                    java.lang.reflect.Field telefoneField = cuidador.getClass().getSuperclass().getDeclaredField("telefone");
                    telefoneField.setAccessible(true);
                    telefoneField.set(cuidador, telefone);

                } catch (Exception e) {
                    System.err.println("Erro no reflection, usando setters normais: " + e.getMessage());

                    cuidador.setIdPessoa(id);
                    cuidador.setNome(nome);
                }

                cuidadores.add(cuidador);
            }

            System.out.println("Total de cuidadores: " + count);
            return cuidadores;

        } catch (SQLException e) {
            System.err.println("ERRO SQL: " + e.getMessage());
            e.printStackTrace();
            throw new InfraestruturaException("Erro ao listar cuidadores", e);
        }
    }

    @Override
    public Cuidador criar(Cuidador cuidador) {
        System.out.println("=== INICIANDO CRIAÇÃO COMPLETA DE CUIDADOR ===");

        Connection conn = null;
        try {
            conn = this.databaseConnection.getConnection();
            conn.setAutoCommit(false);

            Long idCuidador = obterProximoId(conn, "SEQ_CUIDADOR");
            System.out.println("ID do cuidador: " + idCuidador);

            inserirCuidador(conn, idCuidador, cuidador);

            if (cuidador.getEndereco() != null && !cuidador.getEndereco().trim().isEmpty()) {
                System.out.println("Criando endereço para o cuidador...");

                String[] enderecoParts = parseEndereco(cuidador.getEndereco());

                Long idEndereco = criarEnderecoTransacional(conn,
                        enderecoParts[0],
                        enderecoParts[1],
                        enderecoParts[2],
                        enderecoParts[3],
                        enderecoParts[4],
                        enderecoParts[5],
                        enderecoParts[6]
                );

                vincularEnderecoTransacional(conn, idCuidador, idEndereco);
            }

            conn.commit();
            cuidador.setIdPessoa(idCuidador);
            System.out.println("Cuidador criado com sucesso! ID: " + idCuidador);
            return cuidador;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("ROLLBACK executado - cuidador NÃO foi criado");
                } catch (SQLException ex) {
                    System.err.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            throw new InfraestruturaException("Erro ao criar cuidador", e);
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
    public void vincularEndereco(Long idCuidador, Long idEndereco) {
        String sql = "INSERT INTO ACPH_CUIDADOR_ENDERECO (ACPH_CUIDADOR_ID_CUIDADOR, ACPH_ENDERECO_ID_ENDERECO) VALUES (?, ?)";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCuidador);
            stmt.setLong(2, idEndereco);

            System.out.println("Vinculando cuidador " + idCuidador + " com endereço " + idEndereco);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao vincular endereço ao cuidador");
            }

            System.out.println("Endereço " + idEndereco + " vinculado ao cuidador " + idCuidador + " com sucesso!");

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao vincular endereço ao cuidador", e);
        }
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

    private void inserirCuidador(Connection conn, Long idCuidador, Cuidador cuidador) throws SQLException {
        String sql = """
        INSERT INTO ACPH_CUIDADOR
        (id_cuidador, nm_cuidador, sexo_cuidador, cpf_cuidador, dt_nascimento_cuidador,
         tel_cuidador, email_cuidador, senha_cuidador, VERSION)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCuidador);
            stmt.setString(2, cuidador.getNome());
            stmt.setString(3, String.valueOf(cuidador.getSexo()));
            stmt.setString(4, cuidador.getCpf());

            if (cuidador.getDataNascimento() != null) {
                cuidador.setDataNascimento(cuidador.getDataNascimento());
                java.sql.Date sqlDate = java.sql.Date.valueOf(cuidador.getDataNascimento());
                stmt.setDate(3, sqlDate);
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(6, cuidador.getTelefone());
            stmt.setString(7, cuidador.getEmail());
            stmt.setString(8, cuidador.getSenha());
            stmt.setLong(9, cuidador.getVersao() != null ? cuidador.getVersao() : 1L);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao criar cuidador, nenhuma linha foi inserida.");
            }

            System.out.println("Cuidador inserido com sucesso: " + cuidador.getNome());
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

    private void vincularEnderecoTransacional(Connection conn, Long idCuidador, Long idEndereco) throws SQLException {
        String sql = "INSERT INTO ACPH_CUIDADOR_ENDERECO (ACPH_CUIDADOR_ID_CUIDADOR, ACPH_ENDERECO_ID_ENDERECO) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCuidador);
            stmt.setLong(2, idEndereco);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao vincular endereço ao cuidador");
            }

            System.out.println("Endereço " + idEndereco + " vinculado ao cuidador " + idCuidador);
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