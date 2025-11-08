package br.com.fiap.acompanha.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

public class Paciente extends Pessoa{

    private String especialidadeAtendimento;
    @JsonIgnore
    private Cuidador cuidador;

    public Paciente() {
        super();
    }

    public Paciente(Long idPessoa, String nome, String cpf, Date dataNascimento, char sexo,
                    String telefone, String endereco, String especialidadeAtendimento, Long versao) {

        super(idPessoa, nome, cpf, dataNascimento, sexo, telefone, endereco, versao);

        this.especialidadeAtendimento = especialidadeAtendimento;
    }

    public String getEspecialidadeAtendimento() {
        return especialidadeAtendimento;
    }

    public void setEspecialidadeAtendimento(String especialidadeAtendimento) {
        this.especialidadeAtendimento = especialidadeAtendimento;
    }

    public Cuidador getCuidador() {
        return cuidador;
    }

    public void setCuidador(Cuidador cuidador) {
        this.cuidador = cuidador;
    }
}

