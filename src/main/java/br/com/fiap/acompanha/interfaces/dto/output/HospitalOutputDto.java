package br.com.fiap.acompanha.interfaces.dto.output;

public class HospitalOutputDto {

    private Long id;
    private String nome;
    private String endereco;

    public HospitalOutputDto() {}

    public HospitalOutputDto(Long id, String nome, String endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
