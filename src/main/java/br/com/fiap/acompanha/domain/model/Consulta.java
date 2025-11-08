package br.com.fiap.acompanha.domain.model;

import java.util.Date;

public class Consulta {
    private Long idConsulta;
    private Date dataConsulta;
    private String horarioConsultaString;
    private String tipoConsulta;
    private String statusConsulta;
    private Cuidador cuidador;
    private Paciente paciente;

    public Consulta() {}

    public Consulta(Long idConsulta, Date dataConsulta, Date horarioConsulta,
                    String tipoConsulta, String statusConsulta,
                    Cuidador cuidador, Paciente paciente) {
        this.idConsulta = idConsulta;
        this.dataConsulta = dataConsulta;
        this.tipoConsulta = tipoConsulta;
        this.statusConsulta = statusConsulta;
        this.cuidador = cuidador;
        this.paciente = paciente;
    }

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }

    public Date getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(Date dataConsulta) { this.dataConsulta = dataConsulta; }

    public String getHorarioConsultaString() { return horarioConsultaString; }
    public void setHorarioConsultaString(String horarioConsultaString) {
        this.horarioConsultaString = horarioConsultaString;
    }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public String getStatusConsulta() { return statusConsulta; }
    public void setStatusConsulta(String statusConsulta) { this.statusConsulta = statusConsulta; }

    public Cuidador getCuidador() { return cuidador; }
    public void setCuidador(Cuidador cuidador) { this.cuidador = cuidador; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
}