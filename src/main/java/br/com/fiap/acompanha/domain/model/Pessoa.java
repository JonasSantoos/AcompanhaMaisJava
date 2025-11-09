package br.com.fiap.acompanha.domain.model;

import br.com.fiap.acompanha.domain.exceptions.ValidacaoDominioException;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;

public abstract class Pessoa {

    private Long idPessoa;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private char sexo;
    private String telefone;
    private String endereco;
    private Long versao;

    public Pessoa(Long idPessoa, String nome, String cpf, LocalDate dataNascimento, char sexo, String telefone, String endereco, Long versao) {
        this.idPessoa = idPessoa;
        this.nome = nome;
        setCpf(cpf);
        this.dataNascimento = dataNascimento;
        setSexo(sexo);
        setTelefone(telefone);
        this.endereco = endereco;
        this.versao = versao;
    }

    public Pessoa(Long idPessoa, String nome, String cpf, LocalDate dataNascimento, char sexo, String telefone, Long versao) {
        this.idPessoa = idPessoa;
        this.nome = nome;
        setCpf(cpf);
        this.dataNascimento = dataNascimento;
        setSexo(sexo);
        setTelefone(telefone);
        this.versao = versao;
    }

    public Pessoa() {}

    public void setCpf(String cpf){
        this.cpf = cpf;
        isCpfValido();
    }

    public void setSexo(char sexo){
        this.sexo = sexo;
        isSexoValido();
    }

    public void setTelefone(String telefone){
        this.telefone = telefone;
        isTelefoneValido();
    }

    private void isCpfValido(){
        if(!this.cpf.matches("\\d{11}")){
            throw new ValidacaoDominioException("CPF inválido!");
        }
    }

    private void isSexoValido() {
        if (this.sexo != 'M' && this.sexo != 'F') {
            throw new ValidacaoDominioException("Sexo inválido! Deve ser 'M' ou 'F'.");
        }
    }

    private void isTelefoneValido(){
        if(!this.telefone.matches("\\d{11}")){
            throw new ValidacaoDominioException("Telefone inválido!");
        }
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public char getSexo() {
        return sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public Long getVersao() {
        return versao;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }
}