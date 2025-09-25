package br.com.fiap.acompanha.domain.model;

public class Cuidador extends Pessoa{

    private String email;
    private String senha;

    public Cuidador(Long idPessoa, String nome, String cpf, String dataNascimento, char sexo, String telefone, Endereco endereco, String email, String senha, Long versao) {
        super(idPessoa, nome, cpf, dataNascimento, sexo, telefone, endereco, versao);
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
