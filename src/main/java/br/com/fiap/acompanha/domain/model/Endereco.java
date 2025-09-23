package br.com.fiap.acompanha.domain.model;

import br.com.fiap.acompanha.domain.exceptions.ValidacaoDominioException;

public class Endereco {

    private String cep;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String estado;
    private String cidade;


    //Criando Construtor para endereço
    public Endereco(String cep,
                    String rua,
                    String numero,
                    String complemento,
                    String bairro,
                    String estado,
                    String cidade) {

        setCep(cep);
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.estado = estado;
        this.cidade = cidade;
    }

    private void setCep(String cep){
        this.cep = cep;
        isCepValido();
    }

    //Metod que verifica se o CEP é válido
    private void isCepValido(){
        if(!this.cep.matches("\\d{8}")){
            throw new ValidacaoDominioException("CEP inválido!");
        }
    }

    public String getCep() {
        return cep;
    }

    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getEstado() {
        return estado;
    }

    public String getCidade() {
        return cidade;
    }

}
