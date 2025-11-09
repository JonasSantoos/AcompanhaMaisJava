package br.com.fiap.acompanha.domain.model;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Cuidador extends Pessoa{

    private String email;
    private String senha;
    private List<Paciente> pacientes;

    public Cuidador() {
        super();
    }

    public Cuidador(Long idPessoa, String nome, String cpf, LocalDate dataNascimento, char sexo, String telefone, String endereco, String email, String senha, Long versao) {
        super(idPessoa, nome, cpf, dataNascimento, sexo, telefone, endereco, versao);
        this.email = email;
        this.senha = senha;
    }

    public Cuidador(Long idPessoa, String nome, String cpf, LocalDate dataNascimento, char sexo, String telefone, String email, String senha, Long versao) {
        super(idPessoa, nome, cpf, dataNascimento, sexo, telefone, versao);
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }


}

