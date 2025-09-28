package br.com.fiap.acompanha.infrastructure.persistence;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Hospital;
import br.com.fiap.acompanha.domain.repository.HospitalRepository;
import br.com.fiap.acompanha.infrastructure.exceptions.InfraestruturaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcHospitalRepository implements HospitalRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcHospitalRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Hospital salvar(Hospital hospital) {

        String sql = "INSERT INTO ACPH_HOSPITAL " +
                "(id_hospital, nm_hospital, cnpj_hospital, email_corporativo, tel_hospital, qtd_pacientes, version) " +
                "VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, hospital.getId());
            stmt.setString(2, hospital.getNome());
            stmt.setString(3, hospital.getCnpj());
            stmt.setString(4, hospital.getEmailCorporativo());
            stmt.setString(5, hospital.getTelefone());
            stmt.setInt(6, hospital.getQtdPacientes());
            stmt.setLong(7, hospital.getVersao());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao salvar, nenhuma linha do banco foi afetada");
            }

            return hospital;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao salvar hospital", e);
        }
    }

    @Override
    public Hospital buscarPorCnpj(String cnpj) throws EntidadeNaoLocalizada {

        String sql = "SELECT H.ID_HOSPITAL, H.NM_HOSPITAL, H.CNPJ_HOSPITAL, H.EMAIL_CORPORATIVO, H.TEL_HOSPITAL, H.QTD_PACIENTES, H.VERSION, " +
                "E.RUA || ', ' || E.NUMERO || ' - ' || E.BAIRRO AS ENDERECO_COMPLETO " +
                "FROM ACPH_HOSPITAL H " +
                "LEFT JOIN ACPH_HOSPITAL_ENDERECO HE ON H.ID_HOSPITAL = HE.ACPH_HOSPITAL_ID_HOSPITAL " +
                "LEFT JOIN ACPH_ENDERECO E ON HE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO " +
                "WHERE H.CNPJ_HOSPITAL = ? " +
                "FETCH FIRST 1 ROW ONLY";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id_hospital");
                String nome = resultSet.getString("nm_hospital");
                String cnpjFromDB = resultSet.getString("cnpj_hospital");
                String endereco = resultSet.getString("ENDERECO_COMPLETO");
                String emailCorporativo = resultSet.getString("email_corporativo");
                Long versao = resultSet.getLong("VERSION");

                resultSet.close();

                return new Hospital(id, nome, cnpjFromDB, endereco, emailCorporativo, versao);
            }

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar hospital por cnpj", e);
        }
        throw new EntidadeNaoLocalizada("Hospital não encontrado");
    }

    @Override
    public List<Hospital> buscarTodos() {

        String sql = "SELECT H.ID_HOSPITAL, H.NM_HOSPITAL, H.CNPJ_HOSPITAL, H.EMAIL_CORPORATIVO, H.TEL_HOSPITAL, H.QTD_PACIENTES, H.VERSION, " +
                "E.RUA || ', ' || E.NUMERO || ' - ' || E.BAIRRO AS ENDERECO_COMPLETO " +
                "FROM ACPH_HOSPITAL H " +
                "LEFT JOIN ACPH_HOSPITAL_ENDERECO HE ON H.ID_HOSPITAL = HE.ACPH_HOSPITAL_ID_HOSPITAL " +
                "LEFT JOIN ACPH_ENDERECO E ON HE.ACPH_ENDERECO_ID_ENDERECO = E.ID_ENDERECO";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Hospital> hospitais = new ArrayList<>();

            while (rs.next()) {
                Long id = rs.getLong("id_hospital");
                String nome = rs.getString("nm_hospital");
                String cnpj = rs.getString("cnpj_hospital");
                String endereco = rs.getString("ENDERECO_COMPLETO");
                String emailCorporativo = rs.getString("email_corporativo");
                Long versao = rs.getLong("VERSION");

                hospitais.add(new Hospital(id, nome, cnpj, endereco, emailCorporativo, versao));
            }

            return hospitais;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar todos os hospitais", e);
        }
    }

    @Override
    public Hospital excluirHospital(String cnpj, Long versao) throws EntidadeNaoLocalizada{

        Hospital hospitalExistente = null;
        try {
            hospitalExistente = buscarPorCnpj(cnpj);
        } catch (EntidadeNaoLocalizada e) {
            throw new InfraestruturaException("Hospital não encontrado para exclusão.", e);
        }

        String sql = "DELETE FROM ACPH_HOSPITAL WHERE cnpj_hospital = ? AND VERSION = ?";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            stmt.setLong(2, versao);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new InfraestruturaException("Erro ao excluir, nenhuma linha do banco foi afetada ou versão incorreta");
            }

            return hospitalExistente;

        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao excluir hospital", e);
        }
    }
}