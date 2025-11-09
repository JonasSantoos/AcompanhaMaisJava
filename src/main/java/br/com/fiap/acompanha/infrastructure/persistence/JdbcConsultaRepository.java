package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.model.Consulta;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.ConsultaRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcConsultaRepository implements ConsultaRepository  {

    private final DatabaseConnection databaseConnection;

    public JdbcConsultaRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Consulta> listarTodas() {
        String sql = """
            SELECT 
                c.ID_CONSULTA,
                c.DT_CONSULTA,
                c.HORARIO_CONSULTA,
                c.TIPO_CONSULTA,
                c.STATUS_CONSULTA,
                p.ID_PACIENTE,
                p.NM_PACIENTE,
                p.CPF_PACIENTE,
                p.TEL_PACIENTE,
                cu.ID_CUIDADOR,
                cu.NM_CUIDADOR,
                -- Dados do endereço do paciente
                e.RUA,
                e.NUMERO,
                e.COMPLEMENTO,
                e.BAIRRO,
                e.CIDADE,
                e.ESTADO,
                e.CEP
            FROM ACPH_CONSULTA c
            INNER JOIN ACPH_PACIENTE p ON c.ACPH_PACIENTE_ID_PACIENTE = p.ID_PACIENTE
            INNER JOIN ACPH_CUIDADOR cu ON c.ACPH_CUIDADOR_ID_CUIDADOR = cu.ID_CUIDADOR
            LEFT JOIN ACPH_PACIENTE_ENDERECO pe ON p.ID_PACIENTE = pe.ACPH_PACIENTE_ID_PACIENTE
            LEFT JOIN ACPH_ENDERECO e ON pe.ACPH_ENDERECO_ID_ENDERECO = e.ID_ENDERECO
            ORDER BY c.DT_CONSULTA, c.HORARIO_CONSULTA
            """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            List<Consulta> consultas = new ArrayList<>();

            while (resultSet.next()) {
                Consulta consulta = mapearConsultaComEndereco(resultSet);
                consultas.add(consulta);
            }

            return consultas;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar consultas", e);
        }
    }

    @Override
    public List<Consulta> listarPorCuidador(Long idCuidador) {
        String sql = """
    SELECT * FROM (
        SELECT 
            c.ID_CONSULTA,
            c.DT_CONSULTA,
            c.HORARIO_CONSULTA,
            c.TIPO_CONSULTA,
            c.STATUS_CONSULTA,
            p.ID_PACIENTE,
            p.NM_PACIENTE,
            p.CPF_PACIENTE,
            p.TEL_PACIENTE,
            cu.ID_CUIDADOR,
            cu.NM_CUIDADOR,
            cu.CPF_CUIDADOR,
            cu.DT_NASCIMENTO_CUIDADOR,
            cu.SEXO_CUIDADOR,
            e.RUA,
            e.NUMERO,
            e.COMPLEMENTO,
            e.BAIRRO,
            e.CIDADE,
            e.ESTADO,
            e.CEP,
            -- Numera as linhas para cada consulta
            ROW_NUMBER() OVER (PARTITION BY c.ID_CONSULTA ORDER BY e.ID_ENDERECO) as rn
        FROM ACPH_CONSULTA c
        INNER JOIN ACPH_PACIENTE p ON c.ACPH_PACIENTE_ID_PACIENTE = p.ID_PACIENTE
        INNER JOIN ACPH_CUIDADOR cu ON c.ACPH_CUIDADOR_ID_CUIDADOR = cu.ID_CUIDADOR
        LEFT JOIN ACPH_PACIENTE_ENDERECO pe ON p.ID_PACIENTE = pe.ACPH_PACIENTE_ID_PACIENTE
        LEFT JOIN ACPH_ENDERECO e ON pe.ACPH_ENDERECO_ID_ENDERECO = e.ID_ENDERECO
        WHERE cu.ID_CUIDADOR = ?
    ) 
    WHERE rn = 1  -- Pega apenas a primeira linha de cada consulta
    ORDER BY DT_CONSULTA, HORARIO_CONSULTA
    """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCuidador);
            ResultSet resultSet = stmt.executeQuery();

            List<Consulta> consultas = new ArrayList<>();

            while (resultSet.next()) {
                Consulta consulta = mapearConsultaComEndereco(resultSet);
                consultas.add(consulta);
            }

            return consultas;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar consultas do cuidador", e);
        }
    }

    private Consulta mapearConsultaComEndereco(ResultSet resultSet) throws SQLException {
       
        Long idConsulta = resultSet.getLong("ID_CONSULTA");
        Date dataConsulta = resultSet.getDate("DT_CONSULTA");

        // Formatar horário
        Timestamp horarioTimestamp = resultSet.getTimestamp("HORARIO_CONSULTA");
        String horaFormatada = "N/A";
        if (horarioTimestamp != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            horaFormatada = timeFormat.format(new Date(horarioTimestamp.getTime()));
        }

        String tipoConsulta = resultSet.getString("TIPO_CONSULTA");
        String statusConsulta = resultSet.getString("STATUS_CONSULTA");

        // Mapear paciente com endereço
        Paciente paciente = new Paciente();
        paciente.setIdPessoa(resultSet.getLong("ID_PACIENTE"));
        paciente.setNome(resultSet.getString("NM_PACIENTE"));
        paciente.setCpf(resultSet.getString("CPF_PACIENTE"));
        paciente.setTelefone(resultSet.getString("TEL_PACIENTE"));

        // Construir e setar o endereço completo
        String enderecoCompleto = construirEnderecoCompleto(resultSet);
        paciente.setEndereco(enderecoCompleto);

        // Mapear cuidador
        Cuidador cuidador = new Cuidador();
        cuidador.setIdPessoa(resultSet.getLong("ID_CUIDADOR"));
        cuidador.setNome(resultSet.getString("NM_CUIDADOR"));

        // Adicionar dados extras se disponíveis (para listarPorCuidador)
        try {
            cuidador.setCpf(resultSet.getString("CPF_CUIDADOR"));

            java.sql.Date dataNascimentoSql = resultSet.getDate("DT_NASCIMENTO_CUIDADOR");
            if (dataNascimentoSql != null) {
                cuidador.setDataNascimento(dataNascimentoSql.toLocalDate());
            }

            String sexoStr = resultSet.getString("SEXO_CUIDADOR");
            if (sexoStr != null && !sexoStr.isEmpty()) {
                cuidador.setSexo(sexoStr.charAt(0));
            }
        } catch (SQLException e) {
            // Campos podem não estar presentes em todas as consultas
        }

        // Criar e retornar consulta
        Consulta consulta = new Consulta();
        consulta.setIdConsulta(idConsulta);
        consulta.setDataConsulta(dataConsulta);
        consulta.setHorarioConsultaString(horaFormatada);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setStatusConsulta(statusConsulta);
        consulta.setCuidador(cuidador);
        consulta.setPaciente(paciente);

        return consulta;
    }

    private String construirEnderecoCompleto(ResultSet resultSet) throws SQLException {
        StringBuilder endereco = new StringBuilder();

        String rua = resultSet.getString("RUA");
        String numero = resultSet.getString("NUMERO");
        String complemento = resultSet.getString("COMPLEMENTO");
        String bairro = resultSet.getString("BAIRRO");
        String cidade = resultSet.getString("CIDADE");
        String estado = resultSet.getString("ESTADO");
        String cep = resultSet.getString("CEP");

        // Construir endereço formatado
        if (rua != null && !rua.trim().isEmpty()) {
            endereco.append(rua);
            if (numero != null && !numero.trim().isEmpty()) {
                endereco.append(", ").append(numero);
            }
            if (complemento != null && !complemento.trim().isEmpty()) {
                endereco.append(" - ").append(complemento);
            }
        }

        if (bairro != null && !bairro.trim().isEmpty()) {
            if (endereco.length() > 0) endereco.append(" - ");
            endereco.append(bairro);
        }

        if (cidade != null && !cidade.trim().isEmpty()) {
            if (endereco.length() > 0) endereco.append(", ");
            endereco.append(cidade);
        }

        if (estado != null && !estado.trim().isEmpty()) {
            if (endereco.length() > 0) endereco.append("/");
            endereco.append(estado);
        }

        if (cep != null && !cep.trim().isEmpty()) {
            if (endereco.length() > 0) endereco.append(" - CEP: ");
            endereco.append(cep);
        }

        return endereco.length() > 0 ? endereco.toString() : "Endereço não informado";
    }

    private Consulta mapearConsultaCompleta(ResultSet resultSet) throws SQLException {
        return mapearConsultaComEndereco(resultSet);
    }
}