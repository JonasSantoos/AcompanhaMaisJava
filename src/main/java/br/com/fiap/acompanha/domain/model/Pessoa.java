package br.com.fiap.acompanha.domain.model;

import br.com.fiap.acompanha.domain.exceptions.ValidacaoDominioException;
public abstract class Pessoa {

    private Long idPessoa;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private char sexo;
    private String telefone;
    private String endereco;
    private Long versao;
    

    public Pessoa(Long idPessoa, String nome, String cpf, String dataNascimento, char sexo, String telefone, String endereco, Long versao) {

        this.idPessoa = idPessoa;
        this.nome = nome;
        setCpf(cpf);
        this.dataNascimento = dataNascimento;
        setSexo(sexo);
        setTelefone(telefone);
        this.endereco = endereco;
        this.versao = versao;
    }

    //seta cpf e verifica s eé válido
    private void setCpf(String cpf){
        this.cpf = cpf;
        isCpfValido();
    }

    //seta sexo e verifica s eé válido
    private void setSexo(char sexo){
        this.sexo = sexo;
        isSexoValido();
    }

    //seta telefone e verifica s eé válido
    private void setTelefone(String telefone){
        this.telefone = telefone;
        isTelefoneValido();
    }


    //Métod para verificar se CPF é válido
    private void isCpfValido(){
        if(!this.cpf.matches("\\d{11}")){
            throw new ValidacaoDominioException("CPF inválido!");
        }
    }

    //Métod para verificar se o sexo é válido
    private void isSexoValido() {
        if (this.sexo != 'M' && this.sexo != 'F') {
            throw new ValidacaoDominioException("Sexo inválido! Deve ser 'M' ou 'F'.");
        }
    }

    //Métod para verificar se o telefone possui todos dígitos
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

    public String getDataNascimento() {
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
}
