package br.com.fiap.acompanha.interfaces.dto.output;

public class CuidadorOutputDto {

    private Long id;
    private String nome;
    private char sexo;
    private String telefone;
    private String email;
    private String endereco;

    public CuidadorOutputDto() {}

    public CuidadorOutputDto(Long id, String nome, char sexo, String telefone, String email, String endereco) {
        this.id = id;
        this.nome = nome;
        this.sexo = sexo;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}

    public void setNome(String nome) {this.nome = nome;}

    public char getSexo() {return sexo;}

    public void setSexo(char sexo) {this.sexo = sexo;}

    public String getTelefone() {return telefone;}

    public void setTelefone(String telefone) {this.telefone = telefone;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getEndereco() {return endereco;}

    public void setEndereco(String endereco) {this.endereco = endereco;}
}
