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
                cu.ID_CUIDADOR,
                cu.NM_CUIDADOR
            FROM ACPH_CONSULTA c
            INNER JOIN ACPH_PACIENTE p ON c.ACPH_PACIENTE_ID_PACIENTE = p.ID_PACIENTE
            INNER JOIN ACPH_CUIDADOR cu ON c.ACPH_CUIDADOR_ID_CUIDADOR = cu.ID_CUIDADOR
            ORDER BY c.DT_CONSULTA, c.HORARIO_CONSULTA
            """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            List<Consulta> consultas = new ArrayList<>();

            while (resultSet.next()) {
                Consulta consulta = mapearConsultaCompleta(resultSet);
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
        SELECT 
            c.ID_CONSULTA,
            c.DT_CONSULTA,
            c.HORARIO_CONSULTA,
            c.TIPO_CONSULTA,
            c.STATUS_CONSULTA,
            p.ID_PACIENTE,
            p.NM_PACIENTE,
            p.CPF_PACIENTE,
            cu.ID_CUIDADOR,
            cu.NM_CUIDADOR,
            cu.CPF_CUIDADOR,
            cu.DT_NASCIMENTO_CUIDADOR,
            cu.SEXO_CUIDADOR
        FROM ACPH_CONSULTA c
        INNER JOIN ACPH_PACIENTE p ON c.ACPH_PACIENTE_ID_PACIENTE = p.ID_PACIENTE
        INNER JOIN ACPH_CUIDADOR cu ON c.ACPH_CUIDADOR_ID_CUIDADOR = cu.ID_CUIDADOR
        WHERE cu.ID_CUIDADOR = ?
        ORDER BY c.DT_CONSULTA, c.HORARIO_CONSULTA
        """;

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCuidador);
            ResultSet resultSet = stmt.executeQuery();

            List<Consulta> consultas = new ArrayList<>();

            while (resultSet.next()) {
                Consulta consulta = mapearConsultaCompleta(resultSet);
                consultas.add(consulta);
            }

            return consultas;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar consultas do cuidador", e);
        }
    }

    private Consulta mapearConsultaCompleta(ResultSet resultSet) throws SQLException {
        Long idConsulta = resultSet.getLong("ID_CONSULTA");

        Date dataConsulta = resultSet.getDate("DT_CONSULTA");

        Timestamp horarioTimestamp = resultSet.getTimestamp("HORARIO_CONSULTA");
        String horaFormatada = "N/A";

        if (horarioTimestamp != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            horaFormatada = timeFormat.format(new Date(horarioTimestamp.getTime()));
        }

        String tipoConsulta = resultSet.getString("TIPO_CONSULTA");
        String statusConsulta = resultSet.getString("STATUS_CONSULTA");


        Long idPaciente = resultSet.getLong("ID_PACIENTE");
        String nomePaciente = resultSet.getString("NM_PACIENTE");

        Paciente paciente = new Paciente();
        paciente.setIdPessoa(idPaciente);
        paciente.setNome(nomePaciente);


        Long idCuidador = resultSet.getLong("ID_CUIDADOR");
        String nomeCuidador = resultSet.getString("NM_CUIDADOR");

        Cuidador cuidador = new Cuidador();
        cuidador.setIdPessoa(idCuidador);
        cuidador.setNome(nomeCuidador);


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
}