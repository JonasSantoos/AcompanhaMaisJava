package br.com.fiap.acompanha.domain.model;

import br.com.fiap.acompanha.domain.exceptions.ValidacaoDominioException;

public class Hospital {

    private Long id;
    private String nome;
    private String cnpj;
    private String emailCorporativo;
    private Endereco endereco;
    private Long versao;

    public Hospital(Long id, String nome, String cnpj, String emailCorporativo, Endereco endereco, Long versao) {
        this.id = id;
        this.nome = nome;
        setCnpj(cnpj);
        this.emailCorporativo = emailCorporativo;
        this.endereco = endereco;
        this.versao = versao;
    }

    //Seta cnpj e usa o metod para verificar se é válido
    public void setCnpj(String cnpj){
        this.cnpj = cnpj;
        isCnpjValido();
    }

    //Verifica se CNPJ é válido
    public void isCnpjValido(){
        if(!this.cnpj.matches("\\d{14}")){
            throw new ValidacaoDominioException("Cnpj inválido.");
        }

    }


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Long getVersao() {
        return versao;
    }
}
