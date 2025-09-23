package br.com.fiap.acompanha.domain.model;

public class Cuidador extends Pessoa{

    private String email;
    private String senha;

    public Cuidador(String nome, String cpf, String dataNascimento, char sexo, String telefone, Endereco endereco, String email, String senha) {
        super(nome, cpf, dataNascimento, sexo, telefone, endereco);
        this.email = email;
        this.senha = senha;
    }



    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
