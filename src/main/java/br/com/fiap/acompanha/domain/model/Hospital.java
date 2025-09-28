package br.com.fiap.acompanha.domain.model;

public class Hospital {

    private Long id;
    private String nome;
    private String cnpj;
    private String emailCorporativo;
    private String endereco;
    private String telefone;
    private int qtdPacientes;
    private Long versao;

    public Hospital(Long id, Long versao, int qtdPacientes, String telefone, String endereco, String emailCorporativo, String cnpj, String nome) {
        this.id = id;
        this.versao = versao;
        this.qtdPacientes = qtdPacientes;
        this.telefone = telefone;
        this.endereco = endereco;
        this.emailCorporativo = emailCorporativo;
        this.cnpj = cnpj;
        this.nome = nome;
    }


    public Hospital(Long id, String nome, String cnpj, String endereco, String emailCorporativo, Long versao) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.emailCorporativo = emailCorporativo;
        this.versao = versao;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }



    public String getCnpj() {
        return cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public Long getVersao() {
        return versao;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public String getTelefone() {
        return telefone;
    }

    public int getQtdPacientes() {
        return qtdPacientes;
    }
}